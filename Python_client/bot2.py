from .bot import DummyBot
from random import random


class Bot(DummyBot):
    """basic implementation of bot"""
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.move(random() * 1000, random() * 1000)

    def update(self):
        super().update()
