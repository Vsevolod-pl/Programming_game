def run_command(bots):
    """
    :param: bots - list of bots
    :return: True if there is someone alive, else returns False
    """
    alive = False
    for bot in bots:
        bot.update()
        alive = alive or bot.alive
    return alive


def create_command(command_name, number, bot_class):
    """
    :param: command_name - name of command, number - number of bots in command
    :param: number - size of command
    :param: bot_class - class of bot
    :return: list of bots
    """
    bs = [bot_class(name=command_name + str(i)) for i in range(number)]
    return bs
