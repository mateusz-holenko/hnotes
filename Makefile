build-frontend:
	echo "Building frontend"

run-frontend:
	echo "Running frontend"
	zellij action new-pane --name "FRONTEND (:4200)" --cwd frontend -- nix-shell --run fish --command 'ng serve'

build-backend-notes:
	echo "Building notes service"

build-backend-users:
	echo "Building users service"

run-backend-notes:
	echo "Running notes service"
	zellij action new-pane --name "NOTES SERVICE (:8080)" --cwd backend/notes_service -- nix-shell --run fish --command './mvnw spring-boot:run'

run-backend-users:
	echo "Running users service"
	zellij action new-pane --name "USERS SERVICE (:8090)" --cwd backend/users_service -- nix-shell --run fish --command './mvnw spring-boot:run'

run-all: run-frontend run-backend-users run-backend-notes
