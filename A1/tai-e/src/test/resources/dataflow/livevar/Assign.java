class Assign {

    int assign(int a, int b, int c) {
        int d = a + b;
        b = d;
        c = a;
        return b;
    }
}

// x = 1

/*
y = x
z = x
...
x = 2
 */