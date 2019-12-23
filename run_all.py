from Python_client.run_commands import run_match
from subprocess import run
import pipes

def run_all(cnames, bot_classnames, command_sizes, path_to_bin="./Processing_server/application.windows32/Processing_server.exe"):
    p1 = pipes.Template()
    f1 = p1.open('pipefile', 'w')
    run(path_to_bin, cwd=path_to_bin.rsplit("/", 1)[0], stdout=f1)
    f2 = open('pipefile', 'r')
    while f2.read() != 'server started\n':
        pass
    f2.close()
    f1.close()
    return run_match(cnames, bot_classnames, command_sizes)