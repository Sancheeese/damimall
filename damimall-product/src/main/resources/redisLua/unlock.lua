-- 获取锁标识
local key = KEYS[1]

-- 获取线程标识
local threadName = ARGV[1]

-- 判断并删除
local val = redis.call("get", key)
if (val == threadName) then
return redis.call("del", key)
end

return 0
