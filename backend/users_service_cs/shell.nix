let
  nixpkgs = fetchTarball "https://github.com/NixOS/nixpkgs/tarball/nixos-24.11";
  pkgs = import nixpkgs { config = {}; overlays = []; };
in

pkgs.mkShellNoCC {
  packages = with pkgs; [
    dotnet-sdk_9
    dotnet-runtime_9
    dotnet-ef
    netcoredbg
    omnisharp-roslyn
  ];
}
