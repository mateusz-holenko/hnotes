all: build-frontend

build-frontend:
	cd ../frontend && nix-shell --run "ng build"

docker-frontend: build-frontend
	rm -rf ./dockerfiles/frontend/html && mkdir ./dockerfiles/frontend/html
	cp -r ../frontend/dist/hnotes/browser/* ./dockerfiles/frontend/html
	cd ./dockerfiles/frontend && docker build -t hnotes.frontend .

k8s-frontend: docker-frontend
	minikube-linux-amd64 kubectl -- delete deployment nginx-deployment --ignore-not-found
	minikube-linux-amd64 image rm hnotes.frontend
	minikube-linux-amd64 image load hnotes.frontend
	minikube-linux-amd64 kubectl -- replace --force -f ./nginx.deployment.yaml

clean-frontend:
	rm -rf ../frontend/dist

build-notes:
	cd ../backend/notes_service && nix-shell --run "mvn package"

docker-notes: build-notes
	cp ../backend/notes_service/target/hnotes-0.0.1-SNAPSHOT.jar ./dockerfiles/notes/hnotes-notes.jar
	cd ./dockerfiles/notes && docker build -t hnotes.notes .
	minikube-linux-amd64 image load hnotes.notes

build-users:
	cd ../backend/users_service && nix-shell --run "mvn package"

docker-users: build-users
	cp ../backend/users_service/target/husers-0.0.1-SNAPSHOT.jar ./dockerfiles/users/hnotes-users.jar
	cd ./dockerfiles/users && docker build -t hnotes.users .
	minikube-linux-amd64 image load hnotes.users

.PHONY: clean-frontend
