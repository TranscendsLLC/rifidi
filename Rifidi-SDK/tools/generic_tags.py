import requests
import sys
import json
from itertools import repeat
import random

#This tool will feed tags into the generic reader

#[{"id":"123456781234567812345678","antenna":1,"timestamp":1483735761055,"reader":"Alien_1","rssi":"123","extrainformation":"Direction:1|Speed:50"},
#{"id":"123456781234567800000000","antenna":1,"timestamp":1483735761055,"reader":"Alien_1","rssi":"123","extrainformation":"Direction:1|Speed:50"}]

def id_generator(size=24, chars='1234567890ABCDEF'):
    return ''.join(random.choice(chars) for _ in range(size))

def createtag(id=id_generator()):
    tag={}
    tag['id']=id
    tag['antenna']=1
    tag['timestamp']=lambda: int(round(time.time() * 1000))
    #tag['reader']='Generic_1'

def inittags():
    tags={}
    for i in repeat(None, 20):
        tag=createtag()
        tags[tag['id']]=tag

def createtags(taglist):
    

ip=sys.argv[1]
portsarg=sys.argv[2]
ports=portsarg.split(',')

data={}
for i in repeat(None, 10000):
    