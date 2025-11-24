// class Solution {
    int[] parent, rank;
    int find(int x){
        if(parent[x] != x) parent[x] = find(parent[x]);
        return parent[x];
    }
    boolean union(int a, int b){
        a = find(a); b = find(b);
        if(a == b) return false;
        if(rank[a] < rank[b]){
            parent[a] = b;
        } else if(rank[b] < rank[a]){
            parent[b] = a;
        } else {
            parent[b] = a;
            rank[a]++;
        }
        return true;
    }

    int[][] up, maxEdge;
    int[] depth;
    List<int[]>[] g;

    void dfs(int u, int p, int w) {
        up[u][0] = p;
        maxEdge[u][0] = w;
        for (int[] e : g[u]) {
            int v = e[0], wt = e[1];
            if (v == p) continue;
            depth[v] = depth[u] + 1;
            dfs(v, u, wt);
        }
    }

    int queryMax(int u, int v) {
        if (depth[u] < depth[v]) {
            int t = u; u = v; v = t;
        }
        int mx = 0;
        int diff = depth[u] - depth[v];
        for (int i = 0; i < 17; i++) {
            if ((diff & (1 << i)) != 0) {
                mx = Math.max(mx, maxEdge[u][i]);
                u = up[u][i];
            }
        }
        if (u == v) return mx;
        for (int i = 16; i >= 0; i--) {
            if (up[u][i] != up[v][i]) {
                mx = Math.max(mx, Math.max(maxEdge[u][i], maxEdge[v][i]));
                u = up[u][i];
                v = up[v][i];
            }
        }
        mx = Math.max(mx, Math.max(maxEdge[u][0], maxEdge[v][0]));
        return mx;
    }

    public int secondMST(int V, int[][] edges) {
        Arrays.sort(edges, (a, b) -> a[2] - b[2]);
        parent = new int[V];
        rank = new int[V];
        for (int i = 0; i < V; i++) parent[i] = i;

        boolean[] used = new boolean[edges.length];
        int mst = 0, cnt = 0;

        for (int i = 0; i < edges.length; i++) {
            if (union(edges[i][0], edges[i][1])) {
                mst += edges[i][2];
                used[i] = true;
                cnt++;
            }
        }
        if (cnt != V - 1) return -1;

        g = new List[V];
        for (int i = 0; i < V; i++) g[i] = new ArrayList<>();

        for (int i = 0; i < edges.length; i++) {
            if (used[i]) {
                int u = edges[i][0], v = edges[i][1], w = edges[i][2];
                g[u].add(new int[]{v, w});
                g[v].add(new int[]{u, w});
            }
        }

        up = new int[V][17];
        maxEdge = new int[V][17];
        depth = new int[V];

        dfs(0, 0, 0);

        for (int j = 1; j < 17; j++) {
            for (int i = 0; i < V; i++) {
                up[i][j] = up[ up[i][j-1] ][j-1];
                maxEdge[i][j] = Math.max(maxEdge[i][j-1], maxEdge[ up[i][j-1] ][j-1]);
            }
        }

        int second = Integer.MAX_VALUE;

        for (int i = 0; i < edges.length; i++) {
            if (used[i]) continue;
            int u = edges[i][0], v = edges[i][1], w = edges[i][2];
            int mx = queryMax(u, v);
            if (mx != w) {
                int candidate = mst - mx + w;
                if (candidate > mst) second = Math.min(second, candidate);
            }
        }

        return second == Integer.MAX_VALUE ? -1 : second;
    }
}
