from bot import Bot
a = int(input())

bs = [Bot() for i in range(a)]

while True:
    for bot in bs:
        bot.update()