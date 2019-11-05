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

	dgw for pgw:localhost 2001

Notice: pgw is Patient's gateway and dgw is Doctor's gateway

Assuming 365 data blocks for a year (each day one block), the optimum size for generalized bloom filter, to keep false positive less than 1%, is 10700 and temporary bloom filter can be as small as 20 bits. 

