from Python_client import create_command, run_command
from importlib import import_module
from typing import List


def run_match(num_commands: int, cnames: List, bot_classnames: List):
    """
    creates commands and play 1 match
    :param num_commands: number of commands
    :param cnames: list of command names,
    :param bot_classnames: list of c
    :return: dict, keys are cnames, value are "win" or "lose"
    """
    commands = []
    alive = [True]*num_commands

    for i in range(num_commands):
        command_name = cnames[i]
        bot_class = import_module(bot_classnames[i])
        commands.append(create_command(command_name, int(input("number of bots: "))))
        cnames.append(command_name)

    while alive.count(True) > 1:
        for i, command in enumerate(commands):
            alive[i] = run_command(command)

    res = dict()
    for i in len(commands):
        if alive[i]:
            res[cnames[i]] = "win"
        else:
            res[cnames[i]] = "lose"
    return res
