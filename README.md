<h4>Robotics Group Project</h4>
This is my robotics group project that I done during my second year at University.

<p>
The robotics group project works with the Player/Stage application which you can run on a Linux environment (The Fedora flavor comes with it pre-intsalled).<br>

Player/Stage is an application which simulates robots in a environment (worlds) in which you create, from example making the robot avoid hitting walls and making
them travel to a designated area within the map.
</p>

<p>
The main aim of our group project was that we had a collection of three robots that have been placed inside a predefined map, and within this map,
there are a number garbage items that have been peppered throughout the map.

The first task for the robot is to build a internal representation of the world that it is in by exploring the map and adding the new places that it
has discovered, while the robot is exploring the map it is simultaneously identifying the garbage items that have been placed within the map 
recording the coordinate positions of the identified item.

After the robot has finished fully exploring the map, the robot will use its front grippers to take hold of the garbage items that were identified in stage one of the
application and move them to designated location that was specified at the start of the application.<br>

The robot will continue to move all the garbage items to the drop off area one-by-one until there are no more garbage items left that it has identified.<br>

The robot uses its fiducial sensor to identify the specific garbage item and the coordinate positions of the item.  
</p>
