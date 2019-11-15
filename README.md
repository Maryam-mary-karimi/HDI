# HDI
Hierarchical Data Integrity Verification

Personal health tracking devices are increasingly replacing expensive health monitoring of patients. Patients can eventually own their medical data, collected from personal health monitoring devices and other resources, store them in variety of cloud services, and make them available to medical service providers of their choice. In such cases, patients will be responsible for ensuring the integrity of retrieved data, from multiple points, whenever doctors need to access these data to provide appropriate medical services. We present a Hierarchical Data Integrity (HDI) approach to verify if the data, sent by health monitoring devices to the cloud, remain unchanged. It is hierarchical as follows: there is a quick verification of the integrity of recent health data (in less than $1 ms$), followed if necessary by a low overhead secure option for verifying the integrity of both recent and historical data (still only in $26.04 ms$). Further, the hierarchy allows granular identification of data units that fail integrity checks. While we do not consider security attacks in detail, it is possible for a patient to periodically (randomly) use the secure process to verify the integrity of data. This reduces the computation, storage, and time of integrity verification as shown by analysis, simulation, and a simple hardware implementation.

The scenario is that the patinet gateway receive data from multiple wearable devices, create some meta data and store the data in the server. Later, when the doctor wants to retrive the data doctors gateway negotiate with patient gate way to verify that the data is has not been changed. It has 3 programs that can be run on 3 devices, including server (consists of 3 virtual servers), patient's gateway and doctor's gateway.   


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

Assuming 365 data blocks for a year (each day one block), the optimum size for generalized bloom filter, to keep false positive less than 1%, is 10700 bits and temporary bloom filter can be as small as 20 bits. 


# Matlab
The Matlab folder contains files used for calculationg false positive and negative in GBF and BF using the formulats in [1].

[1] Laufer, Rafael P., Pedro B. Velloso, and Otto Carlos MB Duarte. "A generalized bloom filter to secure distributed network applications." Computer Networks 55, no. 8 (2011): 1804-1819.

