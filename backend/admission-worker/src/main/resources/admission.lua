-- KEYS[1] = queue key (ZSET)
-- KEYS[2] = active key (String counter)
-- ARGV[1] = capacity

local activeCount = tonumber(redis.call('GET', KEYS[2])) or 0
local capacity = tonumber(ARGV[1])
local freeSlots = capacity - activeCount

if freeSlots <= 0 then
    return {}
end

local popped = redis.call('ZPOPMIN', KEYS[1], freeSlots)
-- ZPOPMIN 결과: [member1, score1, member2, score2, ...] 형태의 flat 배열

local admittedTokens = {}
local count = 0
local i = 1
while i <= #popped do
    table.insert(admittedTokens, popped[i])
    count = count + 1
    i = i + 2
end

if count > 0 then
    redis.call('INCRBY', KEYS[2], count)
end

return admittedTokens