package thunder;

import thunder.util.vector.Vector2D;
import thunder.util.vector.Vector3D;
import thunder.util.vector.VectorDefinition;

public class Camera {
    private Vector3D position;
    private Vector3D velocity;
    private double fov;
    private double pan;
    private double tilt;
    private VectorDefinition direction;

    private double cp;
    private double sp;
    private double ct;
    private double st;

    private double movementSensitivity;
    private double rotationSensitivity;
    double maxTilt;

    private boolean positionChanged;
    private boolean rotationChanged;

    private Vector2D moved;

    public Camera(Vector3D position, double fov, double pan, double tilt) {
        this.position = position;
        this.fov = fov;
        this.pan = pan;
        this.tilt = tilt;
        this.direction = new VectorDefinition(new Vector3D(1, 0, 0), new Vector3D(0, -1, 0), new Vector3D(0, 0, 1));
        this.velocity = new Vector3D(0, 0, 0);

        this.movementSensitivity = 0.04;
        this.rotationSensitivity = 0.0004;
        this.maxTilt = Math.PI / 4.0;

        this.moved = new Vector2D(0,0);

        this.cp = Math.cos(this.pan);
        this.sp = Math.sin(this.pan);
        this.ct = Math.cos(this.tilt);
        this.st = Math.sin(this.tilt);
    }

    public void setPan(double pan) {
        this.pan = pan;
        this.cp = Math.cos(this.pan);
        this.sp = Math.sin(this.pan);
        this.updateDirection();
    }

    public void setTilt(double tilt) {
        this.tilt = tilt;
        this.ct = Math.cos(this.tilt);
        this.st = Math.sin(this.tilt);
        this.updateDirection();
    }

    public void updateDirection() {
        this.direction.forwards = new Vector3D(this.ct*this.cp, this.ct*this.sp, this.st);
        this.direction.right = new Vector3D(this.sp, Math.cos(this.pan + Math.PI), 0.0);
        this.direction.up = new Vector3D(-this.cp*this.st, -this.sp*this.st, this.ct);
    }

    public void updatePosition() {
        this.position.x += this.velocity.x*this.movementSensitivity;
        this.position.y += this.velocity.y*this.movementSensitivity;
        this.position.z += this.velocity.z*this.movementSensitivity;
    }

    public void updateRotation() {
        this.setPan(this.pan + this.moved.x*this.rotationSensitivity);
        this.setTilt(this.tilt + this.moved.y*this.rotationSensitivity);

        this.moved = new Vector2D(0, 0);
    }

    public String get_specs() {
        return String.format("x: %.8f, y: %.8f, z: %.8f, pan: %.8f, tilt: %.8f", this.position.x, this.position.y, this.position.z, this.pan, this.tilt);
    }

    public Vector2D map_point(Vector3D point, Vector2D screen) {
        // Use matrix transformations to find 3d points relative to the camera's perspective
        double rel_x = point.x*cp*ct + point.y*sp*ct + point.z*st - this.position.x*cp*ct - this.position.y*sp*ct - this.position.z*st;
        double rel_y = this.position.x*sp + point.y*cp - this.position.y*cp - point.x*sp;
        double rel_z = -point.x*cp*st - point.y*sp*st + point.z*ct + this.position.x*cp*st + this.position.y*sp*st - this.position.z*ct;

        //System.out.printf("x: %.2f, y: %.2f, z: %.2f%n", rel_x, rel_y, rel_z);

        // Check to see if point is clipped out of view
        if (rel_x < 0.1 || rel_x > 1000) return new Vector2D(-1, -1);

        // Find normalized 2D values for 3D point projected onto plane
        double norm_x = rel_y/(rel_x*Math.tan(this.fov/2.0));
        double norm_y = rel_z/(rel_x*Math.tan(this.fov/2.0));

        // Convert to canvas coordinates
        double x_map = screen.x - (norm_x*screen.x/2.0 + screen.x/2.0);
        double y_map = screen.y - (norm_y*screen.y/2.0 + screen.y/2.0);
        //System.out.printf("x: %.2f, y: %.2f%n", x_map, y_map);

        // Check to see if within FOV
        if (x_map > screen.x || x_map < 0 || y_map > screen.y || y_map < 0) return new Vector2D(-1, -1);

        return new Vector2D(x_map, y_map);
    }

    public double getCp() {
        return cp;
    }

    public double getSp() {
        return sp;
    }

    public double getCt() {
        return ct;
    }

    public double getSt() {
        return st;
    }

    public VectorDefinition getDirection() {return direction;}

    public void setXVel(double vel) {this.velocity.x = vel;}
    public void setYVel(double vel) {this.velocity.y = vel;}
    public void setZVel(double vel) {this.velocity.z = vel;}

    public void setMouseX(double x, Vector2D screen) {this.moved.x += screen.x/2 - x;}
    public void setMouseY(double y, Vector2D screen) {this.moved.y += screen.y/2 - y;}

    public void setPositionChanged(boolean bool) {this.positionChanged = bool;}
    public void setRotationChanged(boolean bool) {this.rotationChanged = bool;}

    public boolean positionChanged(){return this.positionChanged;}
    public boolean rotationChanged(){return this.rotationChanged;}
}
