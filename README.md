# Dev usage

- First clone the repository
- Make sure you have vagrant installed https://www.vagrantup.com/
- Make sure you have virtualbox installed https://www.virtualbox.org/wiki/VirtualBox
- In your terminal, navigate to the project directory
- Make sure you are in the dev branch `git checkout dev`
- In your console run the command `vagrant up`
- You may now start an ssh session with the server by running `vagrant ssh`
- Once in the ssh terminal you may access the database via psql by running
  the command `devpsql`
- OR you may start the server (which will run on your host @ 192.168.33.10/3000)
  by running the command `devrunserver`
- When you are done with your work you can do a `vagrant halt` so that the vm
  shuts down and doesn't consume resources.
- In the future you will want to pull the changes from the repository with
  `git pull` and issue another `vagrant up`


For the application to be previewable in your own device yout first
have to determine what is the ip of the computer you are running the
server on (ipconfig for windows or ifconfig for linux). Next you have
to edit the Constants.java class in the android studio files. You have
to paste your ip instead of the 192.168.33.10 and make the port 3333.