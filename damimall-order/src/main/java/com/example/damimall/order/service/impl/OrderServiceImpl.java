package com.example.damimall.order.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.common.constant.CartConstant;
import com.example.common.constant.order.OrderConstant;
import com.example.common.to.member.MemberInfoTo;
import com.example.common.to.order.OrderItemTo;
import com.example.common.to.seckill.SeckillOrderTo;
import com.example.common.to.ware.SkuStockTo;
import com.example.common.utils.R;
import com.example.damimall.order.entity.OrderItemEntity;
import com.example.damimall.order.entity.PaymentInfoEntity;
import com.example.damimall.order.exception.NoStockException;
import com.example.damimall.order.feign.CartFeign;
import com.example.damimall.order.feign.MemberFeign;
import com.example.damimall.order.feign.ProductFeign;
import com.example.damimall.order.feign.WareFeign;
import com.example.damimall.order.service.OrderItemService;
import com.example.damimall.order.service.PaymentInfoService;
import com.example.damimall.order.to.OrderCreateTo;
import com.example.damimall.order.utils.UserInfoUtils;
import com.example.damimall.order.utils.UserInfoVo;
import com.example.damimall.order.vo.*;
import com.fasterxml.jackson.core.type.TypeReference;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.damimall.order.dao.OrderDao;
import com.example.damimall.order.entity.OrderEntity;
import com.example.damimall.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {
    @Autowired
    OrderDao orderDao;

    @Autowired
    MemberFeign memberFeign;

    @Autowired
    CartFeign cartFeign;

    @Autowired
    WareFeign wareFeign;

    @Autowired
    ProductFeign productFeign;

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    OrderItemService orderItemService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    PaymentInfoService paymentInfoService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo orderConfirm() {
        OrderConfirmVo confirmVo = new OrderConfirmVo();

        UserInfoVo user = UserInfoUtils.getUser();
        if (user == null || user.getUserId() == null) return confirmVo;

//        主线程的请求上下文，要设置到子线程中
        RequestAttributes mainAttributes = RequestContextHolder.getRequestAttributes();

        // 地址信息
        CompletableFuture<Void> addressFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(mainAttributes);
            List<MemberAddressVo> address = memberFeign.getAddressById(user.getUserId());
            confirmVo.setMemberAddressVos(address);
        }, executor);

        // 购物车列表
        CompletableFuture<Void> itemsFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(mainAttributes);
            List<OrderItemVo> items = cartFeign.getUserItems();
            // 获取重量
            List<Long> ids = items.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
            R r = productFeign.getWeight(ids);
            Map<Long, String> weight = r.getData(new TypeReference<Map<Long, String>>() {});
            for (OrderItemVo item : items) {
                item.setWeight(new BigDecimal(weight.getOrDefault(item.getSkuId(), "0")));
            }

            confirmVo.setItems(items);
        }, executor);
        // 库存信息
        CompletableFuture<Void> stockFuture = itemsFuture.thenRunAsync(() -> {
            RequestContextHolder.setRequestAttributes(mainAttributes);
            List<Long> itemIds = confirmVo.getItems()
                    .stream()
                    .map(OrderItemVo::getSkuId)
                    .collect(Collectors.toList());
            R r = wareFeign.queryStock(itemIds);
            List<SkuStockTo> stockTos = r.getData(new TypeReference<List<SkuStockTo>>() {});
            Map<Long, Boolean> stocks = stockTos.stream()
                    .collect(Collectors.toMap(SkuStockTo::getSkuId, to -> to.getStock() > 0));
            confirmVo.setStocks(stocks);
        });

        // 积分信息
        CompletableFuture<Void> integrationFutrue = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(mainAttributes);
            R r = memberFeign.info(user.getUserId());
            MemberInfoTo member = r.getWithKey("member", new TypeReference<MemberInfoTo>() {});
            confirmVo.setIntegration(member.getIntegration());
        }, executor);

        // 设置放重token
        CompletableFuture<Void> tokenFuture = CompletableFuture.runAsync(() -> {
            String token = UUID.randomUUID().toString().replace("-", "");
            confirmVo.setOrderToken(token);
            redisTemplate.opsForValue().set(OrderConstant.ORDER_UNIQUE_TOKEN_PREFIX + user.getUserId(), token);
        }, executor);

        // 其他...

        try {
            CompletableFuture.allOf(addressFuture, integrationFutrue, integrationFutrue, stockFuture, tokenFuture).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return confirmVo;
    }


    @Override
    @Transactional
//    @GlobalTransactional
    public SubmitOrderRespVo submitOrder(OrderSubmitVo orderSubmitVo) {
        SubmitOrderRespVo submitOrderRespVo = new SubmitOrderRespVo();
        submitOrderRespVo.setCode(0);
//        获取当前用户
        UserInfoVo user = UserInfoUtils.getUser();
        if (user == null) {
            submitOrderRespVo.setCode(1);
            return submitOrderRespVo;
        }

//        验证令牌，防止重复提交
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        DefaultRedisScript<Long> getAndDelScript = new DefaultRedisScript<>(script, Long.class);
        Long valid = redisTemplate.execute(getAndDelScript,
                Collections.singletonList(OrderConstant.ORDER_UNIQUE_TOKEN_PREFIX + user.getUserId()),
                orderSubmitVo.getOrderToken());
        // 验证不通过就返回
        if (valid == 0) {
            submitOrderRespVo.setCode(2);
            return submitOrderRespVo;
        }
        // 通过就创建订单
        OrderCreateTo orderCreateTo = createOrder(orderSubmitVo);
        submitOrderRespVo.setOrder(orderCreateTo.getOrderEntity());
        // 验价
        BigDecimal sub = orderSubmitVo.getPayPrice().subtract(orderCreateTo.getOrderEntity().getPayAmount());
        if (Math.abs(sub.doubleValue()) > 0.01) {
            submitOrderRespVo.setCode(3);
            return submitOrderRespVo;
        }
        // 保存数据
        saveOrder(orderCreateTo.getOrderEntity(), orderCreateTo.getOrderItems());
        // 远程调用锁定库存
        Boolean lockSuccess = lockStock(orderCreateTo);
        if (!lockSuccess) {
            submitOrderRespVo.setCode(4);
            throw new NoStockException();
        }
//        int i = 10 / 0;
        // 订单创建成功就发消息
        // TODO 为了让消息一定发出去，可以发送失败可以存在数据库，然后定期扫描再发一遍
        rabbitTemplate.convertAndSend("order-event-exchange", "order.delay", orderCreateTo.getOrderEntity());

        return submitOrderRespVo;
    }

    @Override
    public Integer getStatusBySn(String orderSn) {
        return orderDao.getStatusBySn(orderSn);
    }



    private Boolean lockStock(OrderCreateTo orderCreateTo) {
        WareSkuLockVo lockVo = new WareSkuLockVo();
        lockVo.setOrderSn(orderCreateTo.getOrderEntity().getOrderSn());
        List<OrderItemVo> vos = orderCreateTo.getOrderItems().stream()
                .map(item -> {
                    OrderItemVo vo = new OrderItemVo();
                    vo.setSkuId(item.getSkuId());
                    vo.setCount(item.getSkuQuantity());
                    vo.setTitle(item.getSkuName());
                    return vo;
                })
                .collect(Collectors.toList());
        lockVo.setLocks(vos);
        R r = wareFeign.lockStock(lockVo);
        return r.getCode() == 0;
    }

    private void saveOrder(OrderEntity orderEntity, List<OrderItemEntity> orderItems) {
        save(orderEntity);
        orderItemService.saveBatch(orderItems);
    }

    private OrderCreateTo createOrder(OrderSubmitVo orderSubmitVo) {
        OrderCreateTo orderCreateTo = new OrderCreateTo();

        OrderEntity orderEntity = buildOrder(orderSubmitVo);
        List<OrderItemEntity> orderItems = buildOrderItems(orderEntity.getOrderSn());
        calcOrderPrice(orderEntity, orderItems);

        orderCreateTo.setOrderEntity(orderEntity);
        orderCreateTo.setOrderItems(orderItems);

        return orderCreateTo;
    }

    private OrderEntity buildOrder(OrderSubmitVo orderSubmitVo){
        OrderEntity order = new OrderEntity();

        R r = wareFeign.fare(orderSubmitVo.getAddrId());
        FareVo fareVo = r.getData(new TypeReference<FareVo>() {});
        MemberAddressVo memberAddressVo = fareVo.getMemberAddressVo();
        if (memberAddressVo == null) return order;

        // 设置orderEntity的基本信息
        // 收货信息
        order.setOrderSn(IdWorker.getTimeId());
        order.setMemberId(UserInfoUtils.getUser().getUserId());
        order.setCreateTime(new Date(System.currentTimeMillis()));
        order.setReceiverPhone(memberAddressVo.getPhone());
        order.setReceiverPostCode(memberAddressVo.getPostCode());
        order.setReceiverProvince(memberAddressVo.getProvince());
        order.setReceiverCity(memberAddressVo.getCity());
        order.setReceiverDetailAddress(memberAddressVo.getDetailAddress());
        order.setReceiverRegion(memberAddressVo.getRegion());
        order.setFreightAmount(fareVo.getFare());

        // 初始状态
        order.setConfirmStatus(OrderConstant.OrderStatusEnum.CREATE_NEW.getCode());
        order.setDeleteStatus(0);
        order.setStatus(0);

        return order;
    }

    public List<OrderItemEntity> buildOrderItems(String orderSn) {
        List<OrderItemEntity> orderItemEntities = new ArrayList<>();
        // 查询购物项
        List<OrderItemVo> cartItems = cartFeign.getUserItems();
        if (cartItems == null || cartItems.isEmpty()) return orderItemEntities;

        Map<Long, Integer> id2Count = new HashMap<>();
        Map<Long, String> id2attr = new HashMap<>();
        for (OrderItemVo cartItem : cartItems) {
            id2Count.put(cartItem.getSkuId(), cartItem.getCount());
            String attrs = String.join(";", cartItem.getSkuAttrValues());
            id2attr.put(cartItem.getSkuId(), attrs);
        }

        // 收集id并设置相关信息
        List<Long> skuIds = new ArrayList<>();
        for (OrderItemVo cartItem : cartItems) {
            skuIds.add(cartItem.getSkuId());
        }
        List<OrderItemTo> orderItemTos = productFeign.getOrderSkuInfo(skuIds);
        for (OrderItemTo orderItemTo : orderItemTos) {
            orderItemTo.setOrderSn(orderSn);
            orderItemTo.setSkuQuantity(id2Count.getOrDefault(orderItemTo.getSkuId(), 1));
            orderItemTo.setSkuAttrsVals(id2attr.getOrDefault(orderItemTo.getSkuId(), ""));
            // 真实价格
            BigDecimal multiply = orderItemTo.getSkuPrice().multiply(new BigDecimal(orderItemTo.getSkuQuantity()));
            orderItemTo.setRealAmount(multiply.subtract(orderItemTo.getPromotionAmount()).subtract(orderItemTo.getCouponAmount()).subtract(orderItemTo.getIntegrationAmount()));
            // 积分信息
            orderItemTo.setGiftIntegration(orderItemTo.getGiftIntegration() * orderItemTo.getSkuQuantity());
            orderItemTo.setGiftGrowth(orderItemTo.getGiftGrowth() * orderItemTo.getSkuQuantity());

            OrderItemEntity entity = new OrderItemEntity();
            BeanUtils.copyProperties(orderItemTo, entity);
            orderItemEntities.add(entity);
        }

        return orderItemEntities;
    }

    public void calcOrderPrice(OrderEntity orderEntity, List<OrderItemEntity> orderItems){
        BigDecimal total = new BigDecimal(0);
        BigDecimal payAmount = new BigDecimal(0);
        BigDecimal promotionAmount = new BigDecimal(0);
        BigDecimal couponAmount = new BigDecimal(0);
        BigDecimal integrationAmount = new BigDecimal(0);
        Integer growth = 0;
        Integer integration = 0;
        for (OrderItemEntity item : orderItems) {
            total = total.add(item.getSkuPrice().multiply(new BigDecimal(item.getSkuQuantity())));
            payAmount = payAmount.subtract(item.getCouponAmount()).subtract(item.getPromotionAmount()).subtract(item.getIntegrationAmount());
            promotionAmount = promotionAmount.add(item.getPromotionAmount());
            couponAmount = couponAmount.add(item.getCouponAmount());
            integrationAmount = integrationAmount.add(item.getIntegrationAmount());
            growth += item.getGiftGrowth();
            integration += item.getGiftIntegration();
        }

        payAmount = payAmount.add(total).add(orderEntity.getFreightAmount());
        orderEntity.setTotalAmount(total);
        orderEntity.setPayAmount(payAmount);
        orderEntity.setPromotionAmount(promotionAmount);
        orderEntity.setCouponAmount(couponAmount);
        orderEntity.setIntegrationAmount(integrationAmount);
        orderEntity.setGrowth(growth);
        orderEntity.setIntegration(integration);
    }

    // 超过15分钟未支付关闭订单
    @Override
    @Transactional
    public void colesOrder(OrderEntity order) {
        // 查询该订单的最新信息
        OrderEntity latest = query().eq("order_sn", order.getOrderSn()).one();
        if (latest != null && latest.getStatus().equals(OrderConstant.OrderStatusEnum.CREATE_NEW.getCode())){
            OrderEntity update = new OrderEntity();
            update.setId(latest.getId());
            update.setStatus(OrderConstant.OrderStatusEnum.CANCLED.getCode());
            updateById(update);
            // 再主动发一个消息让库存解锁 TODO 主动给支付宝发送收单请求
            rabbitTemplate.convertAndSend("order-event-exchange", "order.release.other", order.getOrderSn());
        }
    }

    @Override
    public PageUtils listItems(Map<String, Object> param) {
        UserInfoVo user = UserInfoUtils.getUser();
        if (user == null) return null;

        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(param),
                new QueryWrapper<OrderEntity>().eq("member_id", user.getUserId())
        );

        List<OrderEntity> orders = page.getRecords();
        List<String> orderSns = orders.stream().map(OrderEntity::getOrderSn).collect(Collectors.toList());
        Map<String, List<OrderItemEntity>> sn2Item = orderItemService.getSn2Item(orderSns);

        for (OrderEntity order : orders) {
            order.setOrderItemEntityList(sn2Item.getOrDefault(order.getOrderSn(), new ArrayList<>()));
        }
        page.setRecords(orders);

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public String handleAlipay(AliPayAsyncVo payAsyncVo) {
        // 交易信息
        PaymentInfoEntity paymentInfo = new PaymentInfoEntity();
        paymentInfo.setOrderSn(payAsyncVo.getOut_trade_no());
        paymentInfo.setAlipayTradeNo(payAsyncVo.getTrade_no());
        paymentInfo.setSubject(payAsyncVo.getSubject());
        paymentInfo.setCallbackTime(paymentInfo.getCallbackTime());
        paymentInfo.setPaymentStatus(paymentInfo.getPaymentStatus());
        paymentInfo.setCreateTime(new Date());
        paymentInfoService.save(paymentInfo);

        // 更改订单状态
        if (!payAsyncVo.getTrade_status().equals("TRADE_SUCCESS") && !payAsyncVo.getTrade_status().equals("TRADE_FINISHED"))
            return "success";
        String orderSn = payAsyncVo.getOut_trade_no();
        updateStatusBySn(orderSn, OrderConstant.OrderStatusEnum.PAYED.getCode());

        return "success";
    }

    private void updateStatusBySn(String orderSn, Integer code) {
        orderDao.updateStatusBySn(orderSn, code);
    }

    @Override
    public void dealSeckillOrder(SeckillOrderTo order) {
        // 保存订单
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(order.getOrderSn());
        orderEntity.setStatus(OrderConstant.OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setCreateTime(new Date());
        orderEntity.setMemberId(order.getMemberId());
        BigDecimal multiply = order.getSeckillPrice().multiply(new BigDecimal(order.getNum()));
        orderEntity.setPayAmount(multiply);
        this.save(orderEntity);

        // 保存订单项
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.setOrderSn(order.getOrderSn());
        orderItemEntity.setSkuId(order.getSkuId());
        orderItemEntity.setRealAmount(multiply);
        orderItemEntity.setSkuQuantity(order.getNum());
        orderItemService.save(orderItemEntity);
    }
}