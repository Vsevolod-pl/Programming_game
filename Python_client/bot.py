import xml.etree.ElementTree as ET
from socket import socket
from random import random
from math import pi
from time import time

def loadPolygon(xml_polygon):
    """
    :param: xml_polygon: etree element with tag polygon
    :return: list of pairs (x,y) - polygon vertices, clockwork traversal
    """
    res = []
    for v in xml_polygon:
        if v.tag == "vertex":
            res.append((v.attrib['x'],v.attrib['y']))
    return res


def loadMap(filename):
    """
    Returns list of polygon
    Each polygon is represented as list of tuples of vertex coordinats, first is x
    :param: filename: name of xml with map
    """
    res = []
    with open("map.xml","r") as f:
        map = ET.fromstring(f.read())
    for i in map:
        if i.tag == "polygon":
            res.append(loadPolygon(i))
    return res


class DummyBot:
    def __init__(self, name = "bot", ip = "localhost", port = 10002, timeout = 0.1, path_to_map = "map.xml"):
        self.socket = socket()
        self.socket.settimeout(timeout)
        self.socket.connect((ip,port))
        self.socket.send(b"hello")
        ans = self.readInput()
        while "you connected" not in str(ans):
            self.socket.send(b"hello")
            ans = self.readInput()
        self.send(name)
        self.alive = True
        self.command = ""
        self.lastdata = b""
        self.map = loadMap(path_to_map)

    def send(self, string):
        self.socket.send(bytes(string, encoding='utf-8'))

    def readInput(self):
        """
        reads data from server until server sends data
        """
        data = b""
        timedout = False
        while not timedout:
            try:
                data += self.socket.recv(1024)
            except:
                timedout = True
        if data != b"":
            self.lastdata = data
        return data


    def parsedInput(self):
        """
        :return: dictionary, available keys: 'players':[] and 'bullets':[]
        each player is represented as dictionary, available keys: "x":float, "y":float, "hp":float, "name":string
        each bullet is represented as list of coordinats, first is x
        """
        data = self.lastdata
        if data is not None:
            data = data.decode('utf-8')
            data_list = data.split('|')
            res = None
            for i in range(len(data_list)):
                if len(data_list[i]) > 0 and '|'+data_list[i]+'|' in data:
                    res = i

            if res is not None:
                return eval(data_list[res])
        return None

    def update(self):
        """
        calls every tick inherit it in youre class
        """
        if (self.alive):
            self.send(self.command)
            self.command = ""
            self.readInput()
            if "died" in str(self.lastdata):
                self.alive = False

    def shoot(self, angle):
        """
        :param: angle: in radians, 0 is left horisontal direction
        sends command to shoot at angle
        """
        self.command += "shoot("+str(angle)+");"


    def move(self, x, y):
        """
        sends command to move your bot, to position(x,y)
        """
        self.command += 'move('+str(x)+', '+str(y)+');'


class Bot(DummyBot):
    """basic implementation of bot"""
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)

    def update(self):
        self.move(random()*1000, random()*1000)
        self.shoot(random()*pi*2)
        super().update()