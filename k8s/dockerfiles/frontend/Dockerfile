# Create an nginx setup with a static Angular content
FROM nginx:latest

# Copy static content
COPY html/* /usr/share/nginx/html/
COPY default.conf.template /etc/nginx/templates/default.conf.template
