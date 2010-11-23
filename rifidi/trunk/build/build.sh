#!/bin/sh

python ./auto_builder.py
ant package
python package_manager.py -c -d
python package_manager.py --cleanup

