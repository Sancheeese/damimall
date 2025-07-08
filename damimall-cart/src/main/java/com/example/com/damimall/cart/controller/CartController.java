package com.example.com.damimall.cart.controller;

import com.example.com.damimall.cart.service.CartService;
import com.example.com.damimall.cart.vo.Cart;
import com.example.com.damimall.cart.vo.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.jws.WebParam;
import java.util.List;

@Controller
public class CartController {
    @Autowired
    CartService cartService;

    @GetMapping("/cart.html")
    public String toCartHtml(Model model){
//        List<CartItem> items = cartService.getCartItems();
        Cart cart = cartService.getCart();
        model.addAttribute("cart", cart);
        return "cartList";
    }

    @GetMapping("/addCartItem")
    public String addToCart(@RequestParam Long skuId, @RequestParam Integer num, RedirectAttributes redirectAttributes){
        cartService.addToCart(skuId, num);
        redirectAttributes.addAttribute("skuId", skuId);
        return "redirect:http://cart.damimall.com/addToCart.html";
    }

    @GetMapping("/addToCart.html")
    public String addToCartHtml(@RequestParam Long skuId, Model model){
        CartItem cartItem = cartService.getCartItemBySkuId(skuId);
        model.addAttribute("cartItem", cartItem);
        return "success";
    }

    @GetMapping("/checkItem")
    public String checkItem(Long skuId, Integer checked){
        cartService.checkItem(skuId, checked);

        return "redirect:http://cart.damimall.com/cart.html";
    }

    @GetMapping("/countItem")
    public String countItem(Long skuId, Integer num){
        cartService.countItem(skuId, num);

        return "redirect:http://cart.damimall.com/cart.html";
    }

    @GetMapping("/deleteItem")
    public String checkItem(Long skuId){
        cartService.deleteItem(skuId);

        return "redirect:http://cart.damimall.com/cart.html";
    }

    @ResponseBody
    @GetMapping("/getItems")
    public List<CartItem> getUserItems(){
        return cartService.getUserItems();
    }

}
