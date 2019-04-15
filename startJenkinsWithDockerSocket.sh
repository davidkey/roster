# docker build --tag=davidkey/jenkins-docker:lts .

docker run -d -p 8080:8080 -p 50000:50000 -v /var/run/docker.sock:/var/run/docker.sock -v jenkins_home:/var/jenkins_home davidkey/jenkins-docker:lts
