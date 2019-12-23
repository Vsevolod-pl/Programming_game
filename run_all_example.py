from run_all import run_all

numbercommands = int(input("number of commans: "))
cnames = []
bot_classnames = []
command_sizes = []
for i in range(numbercommands):
    cnames.append(input("command name: "))
    bot_classnames.append(input("bot class name: "))
    command_sizes.append(int(input("size of command: ")))

print(run_all(cnames, bot_classnames, command_sizes))