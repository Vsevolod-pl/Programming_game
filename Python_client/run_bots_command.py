from bot import Bot
def run_command(bots):
    """
    :param: bots - list of bots
    :return: True if there is someone alive, else returns False
    """
    alive = False
    for bot in bs:
        bot.update()
        alive = alive or bot.alive
    return alive

def create_command(commandName, number):
    """
    :param: commandName - name of command, number - number of bots in command
    :return: list of bots
    """
    bs = [Bot(name=commandName+str(i)) for i in range(number)]
    return bs