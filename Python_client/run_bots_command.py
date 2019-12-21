from bot import Bot
commandName = input("command name: ")
a = int(input("number of players: "))

bs = [Bot(name=commandName+str(i)) for i in range(a)]

alive = True
while alive:
    alive = False
    for bot in bs:
        bot.update()
        alive = alive or bot.alive
print("all died")