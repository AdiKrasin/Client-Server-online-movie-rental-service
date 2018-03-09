# Flags
CC = g++
CFLAGS  = -std=c++11 
BFLAGS  = -lboost_thread -lboost_system
LFLAGS  = -L/usr/lib

# All Targets
all: BBclient

# Tool invocations
BBclient: bin/TaskWriter.o bin/ConnectionHandler.o bin/BBclient.o
	@echo 'Building targets'
	@echo 'Invoking: C++ Linker'
	$(CC) -o bin/BBClient bin/TaskWriter.o bin/ConnectionHandler.o bin/BBclient.o $(LFLAGS) $(BFLAGS)
	@echo 'Finished building targets'
	@echo ' '
	
# ConnectionHandler
bin/ConnectionHandler.o: src/ConnectionHandler.cpp
	$(CC) $(CFLAGS) -c -Iinclude src/ConnectionHandler.cpp -o bin/ConnectionHandler.o

# TaskWriter
bin/TaskWriter.o: src/TaskWriter.cpp
	$(CC) $(CFLAGS) -c -Iinclude src/TaskWriter.cpp -o bin/TaskWriter.o

# BBclient
bin/BBclient.o: src/BBclient.cpp
	$(CC) $(CFLAGS) -c -Iinclude src/BBclient.cpp -o bin/BBclient.o

# Clean the build directory
clean: 
	rm -f bin/*
