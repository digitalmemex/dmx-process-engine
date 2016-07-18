# DeepaMehta <> Camunda Integration Plugin

## Requirements

Download and unzip the actual DeepaMehta distribution http://download.deepamehta.de/deepamehta-4.8.2.zip

Remove slf4j bundles and start DeepaMehta.

    rm deepamehta-4.8.2/bundle/slf4j-*.jar
    ./deepamehta-4.8.2/deepamehta-linux.sh

## Usage

Clone this repository and download additional bundles.

    cd deepamehta-4.8.2/bundle-dev
    git clone https://github.com/digitalmemex/dmx-process-engine
    cd dmx-process-engine
    sh download-bundles.sh

Deploy additional bundles. 

    mv *.jar ../bundle-deploy

Build and deploy this plugin (local install is needed to build depending plugins)

    mvn clean install

## Test

Get Engine Info

    curl http://localhost:8080/process/engine

