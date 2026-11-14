FROM jenkins/jenkins:lts-jdk21

USER root

RUN apt-get update && \
    apt-get install -y ca-certificates curl docker.io tar gzip && \
    rm -rf /var/lib/apt/lists/*

RUN curl -fL \
    https://mirror.openshift.com/pub/openshift-v4/clients/ocp/4.22.15/openshift-client-linux.tar.gz \
    -o /tmp/openshift-client.tar.gz && \
    tar -xzf /tmp/openshift-client.tar.gz -C /usr/local/bin oc kubectl && \
    chmod +x /usr/local/bin/oc /usr/local/bin/kubectl && \
    rm /tmp/openshift-client.tar.gz

USER jenkins