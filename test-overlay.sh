test_home=<Absolute path to project folder>

for i in `cat machine_list`
	do
		echo 'logging into '${i}
		gnome-terminal -x bash -c "ssh -t ${i} 'cd ${test_home}; java MessagingNode <registry_host> <registry_port>;bash;'" &
	done
