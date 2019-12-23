from Python_client import create_command, run_command

num_commands = int(input())

commands = []
alive = [True]*num_commands
cnames = []

for i in  range(num_commands):
    commandName = input("command name: ")
    commands.append(create_command(commandName, int(input("number of bots: "))))
    cnames.append(commandName)

while alive.count(True) > 1:
    for i, command in enumerate(commands):
        alive[i] = run_command(command)

for i in len(commands):
    if alive[i]:
        print(cnames[i]+" win")
    else:
        print(cnames[i]+" lose")