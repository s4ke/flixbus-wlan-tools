# for a SSH reverse SOCKS proxy set this to something like
# CONNECTION_STRING=ssh -ND <port_for_your_socks_proxy> <server_user>@<proxy_server_ip> -p <port_for_your_ssh_connection>
CONNECTION_STRING="echo 'ERROR: Connection String not set'";
while true; do echo "(Re-)trying ssh connection..."; eval $CONNECTION_STRING; sleep 1; done;