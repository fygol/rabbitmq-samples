See https://github.com/rabbitmq/rabbitmq-tutorials/tree/master/java

### Concurrency
See https://www.rabbitmq.com/api-guide.html

As a rule of thumb, sharing Channel instances between threads is something to be avoided. 
Applications should prefer using a Channel per thread instead of sharing the same Channel across multiple threads. 

While some operations on channels are safe to invoke concurrently, some are not and will result in incorrect frame 
interleaving on the wire, double acknowledgements and so on.
