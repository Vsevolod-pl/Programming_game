import xml.etree.ElementTree as ET
from socket import socket, timeout
from typing import List, Tuple
import os


def load_polygon(xml_polygon):
    """
    :param: xml_polygon: etree element with tag polygon
    :return: list of pairs (x,y) - polygon vertices, clockwork traversal
    """
    res = []
    for v in xml_polygon:
        if v.tag == "vertex":
            res.append((v.attrib['x'], v.attrib['y']))
    return res


def load_map(filename):
    """
    Returns list of polygon
    Each polygon is represented as list of tuples of vertex coordinates, first is x
    :param: filename: name of xml with map
    """
    res = []
    with open("./map.xml", "r") as f:
        map_ = ET.fromstring(f.read())
    for i in map_:
        if i.tag == "polygon":
            res.append(load_polygon(i))
    return res


class DummyBot:
    """
    available fields:
        self.map: Map is represented as list of polygon. Available by self.map.
        Each polygon is represented as list of tuples of vertex coordinates, first is x
        To know position of other bots and bullets call parsed_input().
        Every tick bot sends to server command describing what he wants to do,
        and receives current positions of bullets and bots.
        self.name - you're name
    available functions:
        self.parsedInput() - returns  current positions of bots and bullets
        self.update() - calls every tick, inherit in you're class
        self.shoot(angle) - call to shoot at angle (in radians)
        self.move(x, y) - move to position
    """
    map: List[List[Tuple[float, float]]]
    name: str

    def __init__(self, name="bot", ip="localhost", port=10002, timeout=0.1, path_to_map="map.xml"):
        """
        :param: name - name of bot,
        :param: ip - ip of server,
        :param: port - port of server,
        :param: timeout - time to wait answer from server,
        :param: path_to_map - path to xml file.
        """
        self.socket = socket()
        self.socket.settimeout(timeout)
        self.socket.connect((ip, port))
        self.socket.send(b"hello")
        ans = self.read_input()
        while "you connected" not in str(ans):
            self.socket.send(b"hello")
            ans = self.read_input()
        self.send(name)
        self.alive = True
        self.command = ""
        self.last_data = b""
        self.map = load_map(path_to_map)
        self.name = name

    def send(self, string):
        self.socket.send(bytes(string, encoding='utf-8'))

    def read_input(self):
        """
        reads data from server until server sends data
        """
        data = b""
        timedout = False
        while not timedout:
            try:
                data += self.socket.recv(1024)
            except timeout:
                timedout = True
        if data != b"":
            self.last_data = data
        return data

    def parsed_input(self):
        """
        :return: dictionary, available keys: 'players':[] and 'bullets':[] and 'you' - player
        each player is represented as dictionary, available keys: "x":float, "y":float, "hp":float, "name":string, 'hp':float
        each bullet is represented as list of coordinates, first is x
        """
        data = self.last_data
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
        calls every tick inherit it in you're class
        """
        if self.alive:
            self.send(self.command+";")
            self.command = ""
            self.read_input()
            if "died" in str(self.last_data):
                self.alive = False

    def shoot(self, angle):
        """
        :param: angle: in radians, 0 is left horizontal direction
        sends command to shoot at angle
        """
        self.command += "shoot("+str(angle)+");"

    def move(self, x, y):
        """
        :param: x - x position
        :param: y - y position
        sends command to move your bot, to position(x,y)
        """
        self.command += 'move('+str(x)+', '+str(y)+');'

    def stop(self):
        """
        bot stops moving
        """
        self.command += "stop();"
