mkdir -p target/webroot
ln -s ../../docs target/webroot/OdinCodeBrowser

python3 -m http.server -d target/webroot
