from bot import RandomBot
a = int(input())

bs = [RandomBot() for i in range(a)]

while True:
    for bot in bs:
        bot.update()