from Python_client.run_commands import run_match
from subprocess import run
import os
import time

def run_all(cnames, bot_classnames, command_sizes, path_to_bin = "./Processing_server/application.windows32/Processing_server.exe"):
    run(path_to_bin, cwd=path_to_bin.rsplit("/", 1)[0])
    time.sleep(5)
    return run_match(cnames, bot_classnames, command_sizes)