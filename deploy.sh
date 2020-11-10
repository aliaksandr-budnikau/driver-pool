docker build -t alexanderbudnikov/driver-pool:latest -t alexanderbudnikov/driver-pool:$SHA -f ./Dockerfile ./

docker push alexanderbudnikov/driver-pool:latest

docker push alexanderbudnikov/driver-pool:$SHA