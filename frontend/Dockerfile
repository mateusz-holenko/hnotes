# Create an nginx setup with a static Angular content
FROM nginx:latest

# Copy static content
COPY dist/hnotes/browser/* /usr/share/nginx/html/
COPY docker/default.conf.template /etc/nginx/templates/default.conf.template
