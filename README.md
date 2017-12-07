# Lightstrips

My first java app for android to control lightstrips from [philips hue](www2.meethue.com/en-us/products/lightstrips). 
With this app you can define a sentence of light with different colors in an 
interval of your choice. 

The app offer also a possiblity to save this sentence of lights and start it every time 
at an arbitrary place you want. 

## Background knowledge

Because this project is connected with [siot](https://siot.net/), the instructions to 
change the light will be send first to siot followed by sending the message to raspberry pi with 
MQTT (Message Queuing Telemetry Transport). Raspberry pi will send the instructions to the lightstrips.
