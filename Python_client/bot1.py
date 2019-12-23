from . import bot
from random import random
from math import atan2


class Bot(bot.DummyBot):
    """basic implementation of bot"""
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)

    def update(self):
        self.move(random()*1000, random()*1000)
        sth = self.parsed_input()
        if sth is not None:
            bots = sth['players']
            me = sth['you']
            for bot in bots:
                if bot['name'] != self.name and bot['hp'] > 0:
                    self.shoot(atan2(bot['y']-me['y'], bot['x']-me['x']))
        super().update()
