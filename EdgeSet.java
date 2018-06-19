import java.io.*;
import java.util.*;

public class EdgeSet {

    private static class EdgeStructure {
        public int yMax, xmin;
        public double m;

        public EdgeStructure() {
        }

        public EdgeStructure(int y, int x, double mt) {
            yMax = y;
            xmin = x;
            m = mt;
        }
    }

    static int points[][];
    static int postpoints[][];

    // Vertex Processing
    public static void verproc(int n) {
        int i, prev, next, x, y, tempx;
        for (i = 0; i < n; i++) {
            prev = prevp(n, i);
            next = nextp(n, i);
            x = points[i][0];
            y = points[i][1];
            if (points[next][1] < points[i][1] && points[prev][1] > points[i][1]) {
                System.out.println("===");
                x = xvalue(points[i][0], points[i][1], points[next][0], points[next][1]);
                y = points[i][1] - 1;

            } else if (points[next][1] > points[i][1] && points[prev][1] < points[i][1]) {
                System.out.println("---");
                tempx = xvalue(postpoints[prev][0], postpoints[prev][1], postpoints[prev][2], postpoints[prev][3]);
                postpoints[prev][2] = tempx;
                postpoints[prev][3] -= 1;
            } else {// Local Extremum
            }
            System.out.println("[" + x + "," + y + "]" + "[" + points[next][0] + "," + points[next][1] + "]");
            postpoints[i][0] = x;
            postpoints[i][1] = y;
            postpoints[i][2] = points[next][0];
            postpoints[i][3] = points[next][1];
        }
    }

    // Associated Functions for finding next and prev point
    public static int nextp(int n, int i) {
        int x = (i + 1) % n;
        return (x);
    }

    public static int prevp(int n, int i) {
        int x = (i - 1);
        if (x == -1)
            x = n - 1;
        return (x);
    }

    public static int xvalue(int x1, int y1, int x2, int y2) {
        System.out.println("(" + x1 + "," + y1 + ")" + "(" + x2 + "," + y2 + ")");
        int x;
        x = Math.round((x2 - x1) * (-1) / (y2 - y1) + x1);
        return x;
    }

    public static void main(String[] args) throws IOException {
        int n, i, j, yMax = 0, ymin = 99999, txmin, tymin, tyMax, dx, dy;
        double m;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter number of points:");
        n = Integer.parseInt(br.readLine());
        points = new int[n][2];
        postpoints = new int[n][4];
        System.out.println("Enter the set of points:");
        for (i = 0; i < n; i++) {
            System.out.println("Enter Point " + (i + 1) + ":");
            System.out.print("X-Coordinate:");
            points[i][0] = Integer.parseInt(br.readLine());
            System.out.print("Y-Coordinate:");
            points[i][1] = Integer.parseInt(br.readLine());
            if (points[i][1] > yMax)
                yMax = points[i][1];
            if (points[i][1] < ymin)
                ymin = points[i][1];
        }
        System.out.println("Before Vertex processing:\n-1\tx\ty");
        for (i = 0; i < n; i++) {
            System.out.print(i);
            for (j = 0; j < 2; j++) {
                System.out.print("\t" + points[i][j]);
            }
            System.out.println();
        }
        verproc(n);
        System.out.println("After Vertex processing:\n-1\tx1\ty1\tx2\ty2");
        for (i = 0; i < n; i++) {
            System.out.print(i);
            for (j = 0; j < 4; j++) {
                System.out.print("\t" + postpoints[i][j]);
            }
            System.out.println();
        }

        LinkedList<EdgeStructure>[] vertex = new LinkedList[yMax - ymin];
        for (i = 0; i < n; i++) {
            // Finding Max and Min Points
            if (postpoints[i][3] > postpoints[i][1]) {
                tyMax = postpoints[i][3];
                tymin = postpoints[i][1];
                txmin = postpoints[i][0];
            } else {
                tyMax = postpoints[i][1];
                tymin = postpoints[i][3];
                txmin = postpoints[i][2];
            }
            dx = postpoints[i][2] - postpoints[i][0];
            dy = postpoints[i][3] - postpoints[i][1];
            m = (double) dx / dy;

            // Creating EdgeStructure
            EdgeStructure e = new EdgeStructure(tyMax, txmin, m);
            if (vertex[tymin - ymin] == null) {
                vertex[tymin - ymin] = new LinkedList<EdgeStructure>();
            }
            vertex[tymin - ymin].add(e);
        }

        // Sorting According to xmin
        LinkedList<EdgeStructure> list;
        for (i = 0; i < yMax - ymin; i++) {
            list = vertex[i];
            if (list != null) {
                list.sort((EdgeStructure a, EdgeStructure b) -> {
                    int dec = 0;
                    if (a.xmin > b.xmin)
                        dec = 1;
                    if (a.xmin < b.xmin)
                        dec = -1;
                    return (dec);
                });
            }
        }

        // Displaying EdgeSet
        System.out.println("Final Sorted Edge Table is::");
        for (i = yMax - ymin - 1; i >= 0; i--) {
            System.out.print((ymin + i) + "|");
            if (vertex[i] == null)
                System.out.print("NULL");
            else {
                for (EdgeStructure a : vertex[i]) {
                    EdgeStructure s = a;
                    System.out.print("\t(" + s.yMax + "," + s.xmin + "," + s.m + ")");
                }
            }
            System.out.println();
        }
    }

}