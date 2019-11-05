# HDI
Hierarchical Data Integrity Verification


Each Folder has a DeviceInfo.txt file that specified IP and port

In each "DeviceInfo.txt" file change "localhost" to the corresponding devices' IP


Here are the Ports used in DeviceInfo files:

3 servers with IP and port for pgw and dgw:

HRM, localhost, 4001

activity_tracker, localhost, 4002

calorimeter, localhost, 4003



pgw for server:localhost cloudPort-1000 (3001,3002,3003)

dgw for server:localhost cloudPort+1000 (5001,5002,5003)



pgw for dgw:localhost 3000

dgw for pgw: localhost 2001



bloom filter size= 10700

estimated data blocks in bloom filter 365

temp bloom filter size = 20
