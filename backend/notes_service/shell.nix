let
  nixpkgs = fetchTarball "https://github.com/NixOS/nixpkgs/tarball/nixos-24.05";
  pkgs = import nixpkgs { config = {}; overlays = []; };
in

pkgs.mkShellNoCC {
  DB_HOST="localhost";
  DB_PORT=3306;
  DB_USERNAME="houen";
  DB_PASSWORD="houen";
  VERIFICATION_SERVICE_HOST="localhost";
  VERIFICATION_SERVICE_PORT=5000;

  packages = with pkgs; [
    maven
    jdk17
    jdt-language-server
  ];
}
