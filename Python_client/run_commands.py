from .run_bots_command import create_command, run_command
from importlib import import_module
from typing import List


def run_match(cnames: List, bot_classnames: List, command_sizes: List):
    """
    creates commands and play 1 match
    :param cnames: list of command names,
    :param bot_classnames: list of classnames
    :return: dict, keys are cnames, value are "win" or "lose"
    """
    num_commands = len(cnames)
    commands = []
    alive = [True]*num_commands

    for i in range(num_commands):
        command_name = cnames[i]
        bot_class = import_module(bot_classnames[i]).Bot
        commands.append(create_command(command_name, command_sizes[i], bot_class))
        cnames.append(command_name)

    while alive.count(True) > 1:
        for i, command in enumerate(commands):
            alive[i] = run_command(command)

    res = dict()
    for i in range(len(commands)):
        if alive[i]:
            res[cnames[i]] = "win"
        else:
            res[cnames[i]] = "lose"
    return res
