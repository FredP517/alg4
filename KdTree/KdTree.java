import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;



public class KdTree {
    private Node root;
    private static class Node{
        private Point2D p; //key
        private RectHV rect; //value
        private Node l; //Node left and down
        private Node r; //Node right and up
        private int cnt;
        public Node(Point2D p, RectHV rect, int cnt){
            this.p = p;
            this.rect = rect;
            this.cnt = cnt;
        }
    }
    public KdTree(){
        root = null;
    }
    // construct an empty set of points
    public boolean isEmpty(){
        if(root == null) return true;
        return false;
    }
    // is the set empty?
    public int size(){
        if(isEmpty()) return 0;
        return root.cnt;
    }
    private int size(Node x){
        if(x == null) return 0;
        return x.cnt;
    }
    // number of points in the set
    public void insert(Point2D p){
        if(p == null) throw new IllegalArgumentException();
        root = insert(root, p, 0.0, 0.0, 1.0, 1.0, true); //indicate the range of the rectangle
    }
    // add the point to the set (if it is not already in the set)
    private Node insert(Node x, Point2D p, double x0, double y0, double x1, double y1, boolean isVertical){
        if(x == null) return new Node(p, new RectHV(x0, y0, x1, y1), 1);
        if(x.p.equals(p)) return x;
        if(isVertical){
            //distribute the points to the left or right space
            double cmp = p.x() - x.p.x();
            if(cmp < 0) x.l = insert(x.l, p, x0, y0, x.p.x(),y1,!isVertical); //下一层反转分类方法
            else x.r = insert(x.r, p, x.p.x(), y0, x1, y1, !isVertical);
        }
        else{
            //distribute the points to up or down space
            double cmp = p.y() - x.p.y();
            if(cmp < 0) x.l = insert(x.l, p, x0, y0, x1, x.p.y(), !isVertical);
            else x.r = insert(x.r, p, x0, x.p.y(), x1, y1, !isVertical);
        }
        x.cnt = 1 + size(x.l) + size(x.r);
        return x;
    }
    public boolean contains(Point2D p){
        if(p == null) throw new IllegalArgumentException();
        return contains(root, p, true);
    }
    private boolean contains(Node x, Point2D p, boolean isVertical){
        if(x == null) return false;
        if(x.p.equals(p)) return true;
        if(isVertical){
            double cmp = p.x() - x.p.x();
            if(cmp < 0) return contains(x.l, p, !isVertical);
            else return contains(x.r, p, !isVertical);
        }
        else{
            double cmp = p.y() - x.p.y();
            if(cmp < 0) return contains(x.l, p, !isVertical);
            else return contains(x.r, p, !isVertical);
        }
    }
    // does the set contain point p?
    public void draw(){
        if(isEmpty()) return;
        Queue<Point2D> q = new Queue<>();
        inorder(root, q);
        while (!q.isEmpty()){
            q.dequeue().draw();
        }
    }
    // draw all points to standard draw
    public Iterable<Point2D> range(RectHV rect){
        if(rect == null) throw new IllegalArgumentException();
        Queue<Point2D> q = new Queue<>();
        range(root, q, rect);
        return q;
    }
    private void range(Node x, Queue<Point2D> q, RectHV rect){
        if(x == null) return;
        if(!x.rect.intersects(rect)) return;
        if(rect.contains(x.p)) q.enqueue(x.p);
        range(x.l, q, rect);
        range(x.r, q, rect);
    }
    private void inorder(Node x, Queue<Point2D> q){
        if(x == null) return;
        inorder(x.l, q);
        q.enqueue(x.p);
        inorder(x.r, q);
    }
    // all points that are inside the rectangle (or on the boundary)
    public Point2D nearest(Point2D p){
        if(p == null) throw new IllegalArgumentException();
        if(isEmpty()) return null;
        return nearest(root, p, true, root.p);
    }
    // a nearest neighbor in the set to point p; null if the set is empty
    private Point2D nearest(Node x, Point2D p, boolean isVertical, Point2D c){
        if(x == null) return c;
        Point2D ans = c;
        if(p.distanceTo(x.p) < p.distanceTo(c)) ans = x.p;
        double dist_to_rect_x = x.rect.distanceTo(p);
        if(dist_to_rect_x < p.distanceTo(c)){
            if(isVertical){
                double cmp = p.x() - x.p.x();
                if(cmp < 0){
                    ans = nearest(x.l, p, !isVertical, ans);
                    ans = nearest(x.r, p, !isVertical, ans);
                }
                else{
                    ans = nearest(x.r, p, !isVertical, ans);
                    ans = nearest(x.l, p, !isVertical, ans);
                }
            }
            else{
                double cmp = p.y() - x.p.y();
                if(cmp < 0){
                    ans = nearest(x.l, p, !isVertical, ans);
                    ans = nearest(x.r, p, !isVertical, ans);
                }
                else{
                    ans = nearest(x.r, p, !isVertical, ans);
                    ans = nearest(x.l, p, !isVertical, ans);
                }
            }
        }
        return ans;
    }
}
