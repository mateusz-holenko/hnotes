let
  nixpkgs = fetchTarball "https://github.com/NixOS/nixpkgs/tarball/nixos-24.05";
  pkgs = import nixpkgs { config = {}; overlays = []; };
in

pkgs.mkShellNoCC {
  packages = with pkgs; [
    python3
    ruff
  ];

  shellHook = ''
    source .venv/bin/activate;
    pip install -r requirements.txt
  '';
}
