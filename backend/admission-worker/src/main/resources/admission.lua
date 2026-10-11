-- KEYS[1] = queue key (ZSET)
-- KEYS[2] = active key (ZSET, score = 입장 허가 만료 시각)
-- ARGV[1] = capacity
-- ARGV[2] = now (현재 시각, epoch ms)
-- ARGV[3] = ttl (입장 유효시간, ms)

local now = tonumber(ARGV[2])
local ttl = tonumber(ARGV[3])

-- 유효기간 지난 사람 제거 (이탈 처리)
redis.call('ZREMRANGEBYSCORE', KEYS[2], '-inf', now)

local activeCount = redis.call('ZCARD', KEYS[2])
local capacity = tonumber(ARGV[1])
local freeSlots = capacity - activeCount

if freeSlots <= 0 then
    return {}
end

local popped = redis.call('ZPOPMIN', KEYS[1], freeSlots)
-- ZPOPMIN 결과: [member1, score1, member2, score2, ...] 형태의 flat 배열

local admittedTokens = {}
local expireAt = now + ttl
local i = 1
while i <= #popped do
    local token = popped[i]
    table.insert(admittedTokens, token)
    redis.call('ZADD', KEYS[2], expireAt, token)
    i = i + 2
end

return admittedTokens