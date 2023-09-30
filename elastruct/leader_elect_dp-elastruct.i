typedef long int __off_t;
typedef long int __off64_t;
typedef long int __time_t;
typedef int __clockid_t;
typedef long int __syscall_slong_t;
struct timespec {
    __time_t tv_sec;
    __syscall_slong_t tv_nsec;
} ;
typedef long unsigned int size_t;
struct _IO_FILE ;
struct _IO_FILE ;
typedef struct _IO_FILE FILE;
struct _IO_jump_t ;
struct _IO_FILE ;
typedef void _IO_lock_t;
struct _IO_FILE {
    int _flags;
    char *_IO_read_ptr;
    char *_IO_read_end;
    char *_IO_read_base;
    char *_IO_write_base;
    char *_IO_write_ptr;
    char *_IO_write_end;
    char *_IO_buf_base;
    char *_IO_buf_end;
    char *_IO_save_base;
    char *_IO_backup_base;
    char *_IO_save_end;
    struct _IO_marker *_markers;
    struct _IO_FILE *_chain;
    int _fileno;
    int _flags2;
    __off_t _old_offset;
    unsigned short _cur_column;
    signed char _vtable_offset;
    char _shortbuf[1];
    _IO_lock_t *_lock;
    __off64_t _offset;
    void *__pad1;
    void *__pad2;
    void *__pad3;
    void *__pad4;
    size_t __pad5;
    int _mode;
    char _unused2[15 * sizeof(int) - 4 * sizeof(void *) - sizeof(size_t)];
} ;
struct _IO_FILE_plus ;
extern int fclose(FILE *__stream);
extern FILE *fopen(const char *__restrict __filename, const char *__restrict __modes);
extern int printf(const char *__restrict __format, ...);
extern int fscanf(FILE *__restrict __stream, const char *__restrict __format, ...);
extern int sscanf(const char *__restrict __s, const char *__restrict __format, ...);
extern int fscanf(FILE *__restrict __stream, const char *__restrict __format, ...);
extern int sscanf(const char *__restrict __s, const char *__restrict __format, ...);
typedef __clockid_t clockid_t;
extern int rand(void );
extern void *malloc(size_t __size);
extern void *calloc(size_t __nmemb, size_t __size);
extern void *realloc(void *__ptr, size_t __size);
extern void free(void *__ptr);
extern void *memcpy(void *__restrict __dest, const void *__restrict __src , size_t __n);
extern int strcmp(const char *__s1, const char *__s2);
struct sigevent ;
extern int clock_gettime(clockid_t __clock_id, struct timespec *__tp);
struct stUn_imopVarPre27 {
    void *items;
    size_t object_size;
    int allocated;
    int used;
} ;
typedef struct stUn_imopVarPre27 vector;
void initialize_vector(vector *, size_t );
vector *new_vector(size_t );
void append_to_vector(vector *, void *);
void *elem_at(vector *, size_t );
void free_vector(vector *);
struct stUn_imopVarPre28 {
    vector neighbors;
    int degree;
    void *data;
    int label;
    int weight;
    int parent_label;
} ;
typedef struct stUn_imopVarPre28 node;
struct stUn_imopVarPre29 {
    vector vertices;
    int N;
    int M;
    int **adj_mat;
    node *root;
} ;
typedef struct stUn_imopVarPre29 graph;
graph *new_graph();
int read_graph(graph *, FILE *);
void add_edge(graph *, int  , int );
void free_graph(graph *);
int is_connected(graph *);
void swap(int *, int *);
int input_through_argv(int , char **);
struct timespec start_time;
void begin_timer();
long long time_elapsed();
void swap(int *a, int *b) {
    int t = *a;
    *a = *b;
    *b = t;
}
int input_through_argv(int argc, char *argv[]) {
    if (argc < 3) {
        return 0;
    }
    int _imopVarPre113;
    char *_imopVarPre114;
    int _imopVarPre115;
    _imopVarPre113 = argc == 4;
    if (_imopVarPre113) {
        _imopVarPre114 = argv[1];
        _imopVarPre115 = strcmp(_imopVarPre114, "-");
        _imopVarPre113 = _imopVarPre115 == 0;
    }
    if (_imopVarPre113) {
        return 2;
    }
    return 0;
}
void begin_timer() {
    struct timespec *_imopVarPre117;
    _imopVarPre117 = &start_time;
    clock_gettime(2, _imopVarPre117);
}
long long time_elapsed() {
    struct timespec end_time;
    struct timespec *_imopVarPre119;
    _imopVarPre119 = &end_time;
    clock_gettime(2, _imopVarPre119);
    long long int s = end_time.tv_sec - start_time.tv_sec;
    long long int ns = end_time.tv_nsec - start_time.tv_nsec;
    return (s * ((long long) 1e9)) + ns;
}
void initialize_vector(vector *v, size_t object_size) {
    v->object_size = object_size;
    v->allocated = 16;
    unsigned long int _imopVarPre121;
    void *_imopVarPre122;
    _imopVarPre121 = v->allocated * v->object_size;
    _imopVarPre122 = malloc(_imopVarPre121);
    v->items = _imopVarPre122;
    v->used = 0;
}
vector *new_vector(size_t object_size) {
    unsigned long int _imopVarPre125;
    void *_imopVarPre126;
    _imopVarPre125 = 1 * sizeof(vector);
    _imopVarPre126 = malloc(_imopVarPre125);
    vector *v = (vector *) _imopVarPre126;
    initialize_vector(v, object_size);
    return v;
}
void append_to_vector(vector *v, void *object) {
    while (v->allocated <= v->used + 1) {
        v->allocated *= 2;
        unsigned long int _imopVarPre129;
        void *_imopVarPre130;
        void *_imopVarPre131;
        _imopVarPre129 = v->allocated * v->object_size;
        _imopVarPre130 = v->items;
        _imopVarPre131 = realloc(_imopVarPre130, _imopVarPre129);
        v->items = _imopVarPre131;
    }
    unsigned long int _imopVarPre134;
    void *_imopVarPre135;
    _imopVarPre134 = v->object_size;
    _imopVarPre135 = v->items + (v->used * v->object_size);
    memcpy(_imopVarPre135, object, _imopVarPre134);
    v->used++;
}
void *elem_at(vector *v, size_t idx) {
    return v->items + (idx * v->object_size);
}
void free_vector(vector *v) {
    void *_imopVarPre137;
    _imopVarPre137 = v->items;
    free(_imopVarPre137);
    free(v);
}
graph *new_graph(int N, int M) {
    unsigned long int _imopVarPre139;
    void *_imopVarPre140;
    _imopVarPre139 = 1 * sizeof(graph);
    _imopVarPre140 = malloc(_imopVarPre139);
    graph *g = _imopVarPre140;
    g->N = N;
    g->M = M;
    unsigned long int _imopVarPre143;
    struct stUn_imopVarPre27 *_imopVarPre144;
    _imopVarPre143 = sizeof(node);
    _imopVarPre144 = &g->vertices;
    initialize_vector(_imopVarPre144, _imopVarPre143);
    int i;
    for (i = 0; i < g->N; i++) {
        node u;
        unsigned long int _imopVarPre147;
        struct stUn_imopVarPre27 *_imopVarPre148;
        _imopVarPre147 = sizeof(node *);
        _imopVarPre148 = &u.neighbors;
        initialize_vector(_imopVarPre148, _imopVarPre147);
        u.degree = 0;
        u.data = ((void *) 0);
        u.label = i;
        u.weight = 0;
        struct stUn_imopVarPre28 *_imopVarPre151;
        struct stUn_imopVarPre27 *_imopVarPre152;
        _imopVarPre151 = &u;
        _imopVarPre152 = &g->vertices;
        append_to_vector(_imopVarPre152, _imopVarPre151);
    }
    unsigned long int _imopVarPre155;
    void *_imopVarPre156;
    _imopVarPre155 = N * sizeof(int *);
    _imopVarPre156 = malloc(_imopVarPre155);
    g->adj_mat = (int **) _imopVarPre156;
    for (i = 0; i < g->N; i++) {
        unsigned long int _imopVarPre159;
        void *_imopVarPre160;
        _imopVarPre159 = sizeof(int);
        _imopVarPre160 = calloc(N, _imopVarPre159);
        g->adj_mat[i] = (int *) _imopVarPre160;
    }
    g->root = ((void *) 0);
    ;
    return g;
}
int read_graph(graph *g, FILE *in) {
    int M = 0;
    int _imopVarPre162;
    void *_imopVarPre163;
    _imopVarPre162 = g->N + 2;
    _imopVarPre163 = malloc(_imopVarPre162);
    char *line = _imopVarPre163;
    int i;
    for (i = 0; i < g->N; i++) {
        fscanf(in, "%s", line);
        int j;
        for (j = i; j < g->N; j++) {
            switch (line[j]) {
                case '0': break;
                case '1': M++;
                add_edge(g, i, j);
                break;
            }
        }
    }
    free(line);
    return M / 2;
}
void add_edge(graph *g, int i , int j) {
    struct stUn_imopVarPre27 *_imopVarPre169;
    void *_imopVarPre170;
    _imopVarPre169 = &g->vertices;
    _imopVarPre170 = elem_at(_imopVarPre169, i);
    node *node_u = _imopVarPre170;
    struct stUn_imopVarPre27 *_imopVarPre172;
    void *_imopVarPre173;
    _imopVarPre172 = &g->vertices;
    _imopVarPre173 = elem_at(_imopVarPre172, j);
    node *node_v = _imopVarPre173;
    struct stUn_imopVarPre28 **_imopVarPre176;
    struct stUn_imopVarPre27 *_imopVarPre177;
    _imopVarPre176 = &node_v;
    _imopVarPre177 = &node_u->neighbors;
    append_to_vector(_imopVarPre177, _imopVarPre176);
    struct stUn_imopVarPre28 **_imopVarPre180;
    struct stUn_imopVarPre27 *_imopVarPre181;
    _imopVarPre180 = &node_u;
    _imopVarPre181 = &node_v->neighbors;
    append_to_vector(_imopVarPre181, _imopVarPre180);
    node_u->degree++;
    node_v->degree++;
    g->adj_mat[i][j] = g->adj_mat[j][i] = 1;
}
void free_graph(graph *g) {
    ;
    if (g->adj_mat != ((void *) 0)) {
        int i;
        for (i = 0; i < g->N; i++) {
            int *_imopVarPre183;
            _imopVarPre183 = g->adj_mat[i];
            free(_imopVarPre183);
        }
        int **_imopVarPre185;
        _imopVarPre185 = g->adj_mat;
        free(_imopVarPre185);
    }
}
int is_connected(graph *g) {
    unsigned long int _imopVarPre188;
    int _imopVarPre189;
    void *_imopVarPre190;
    _imopVarPre188 = sizeof(int);
    _imopVarPre189 = g->N;
    _imopVarPre190 = calloc(_imopVarPre189, _imopVarPre188);
    int *visited = _imopVarPre190;
    struct stUn_imopVarPre27 *_imopVarPre192;
    void *_imopVarPre193;
    _imopVarPre192 = &g->vertices;
    _imopVarPre193 = elem_at(_imopVarPre192, 0);
    node *root = _imopVarPre193;
    visited[root->label] = 1;
    unsigned long int _imopVarPre195;
    struct stUn_imopVarPre27 *_imopVarPre196;
    _imopVarPre195 = sizeof(node *);
    _imopVarPre196 = new_vector(_imopVarPre195);
    vector *queue = _imopVarPre196;
    struct stUn_imopVarPre28 **_imopVarPre198;
    _imopVarPre198 = &root;
    append_to_vector(queue, _imopVarPre198);
    int queue_position = 0;
    ;
    while (queue_position < queue->used) {
        void *_imopVarPre202;
        _imopVarPre202 = elem_at(queue, queue_position);
        node *cur = *((node **) _imopVarPre202);
        queue_position++;
        int i;
        for (i = 0; i < cur->degree; i++) {
            struct stUn_imopVarPre27 *_imopVarPre209;
            void *_imopVarPre210;
            _imopVarPre209 = &cur->neighbors;
            _imopVarPre210 = elem_at(_imopVarPre209, i);
            node *neighbor = *((node **) _imopVarPre210);
            if (visited[neighbor->label] > 0) {
                continue;
            }
            visited[neighbor->label] = 1;
            struct stUn_imopVarPre28 **_imopVarPre212;
            _imopVarPre212 = &neighbor;
            append_to_vector(queue, _imopVarPre212);
        }
    }
    int connected = 1;
    int i;
    for (i = 0; i < g->N; i++) {
        if (visited[i] == 0) {
            connected = 0;
        }
    }
    ;
    free_vector(queue);
    free(visited);
    return connected;
}
graph *generate_new_graph(int , int );
graph *generate_new_connected_graph(int , int );
graph *generate_new_graph(int N, int M) {
    struct stUn_imopVarPre29 *_imopVarPre213;
    _imopVarPre213 = new_graph(N, M);
    graph *g = _imopVarPre213;
    ;
    int edges_created = 0;
    while (edges_created < M) {
        int _imopVarPre215;
        _imopVarPre215 = rand();
        int u = _imopVarPre215 % N;
        int _imopVarPre217;
        _imopVarPre217 = rand();
        int v = _imopVarPre217 % N;
        if (u == v) {
            continue;
        }
        if (u > v) {
            int *_imopVarPre220;
            int *_imopVarPre221;
            _imopVarPre220 = &v;
            _imopVarPre221 = &u;
            swap(_imopVarPre221, _imopVarPre220);
        }
        if (g->adj_mat[u][v] != 0) {
            continue;
        }
        int _imopVarPre223;
        _imopVarPre223 = rand();
        g->adj_mat[u][v] = g->adj_mat[v][u] = 1 + _imopVarPre223 % 100;
        add_edge(g, u, v);
        edges_created += 1;
    }
    ;
    return g;
}
graph *generate_new_connected_graph(int N, int M) {
    graph *g = ((void *) 0);
    int attempts = 0;
    int _imopVarPre225;
    do {
        attempts++;
        if (g != ((void *) 0)) {
            free_graph(g);
        }
        ;
        g = generate_new_graph(N, M);
        _imopVarPre225 = is_connected(g);
    } while (!_imopVarPre225);
    ;
    return g;
}
struct stUn_imopVarPre32 {
    int x;
    int new_x;
} ;
typedef struct stUn_imopVarPre32 processor;
void initialize_graph(graph *g, int *xvals) {
    int i;
    for (i = 0; i < g->N; i++) {
        struct stUn_imopVarPre27 *_imopVarPre343;
        void *_imopVarPre344;
        _imopVarPre343 = &g->vertices;
        _imopVarPre344 = elem_at(_imopVarPre343, i);
        node *cur = _imopVarPre344;
        unsigned long int _imopVarPre346;
        void *_imopVarPre347;
        _imopVarPre346 = sizeof(processor);
        _imopVarPre347 = malloc(_imopVarPre346);
        processor *p = _imopVarPre347;
        p->x = xvals[i];
        cur->data = p;
    }
}
int verify_and_print_solution(graph *g) {
    int disagreement = 0;
    int leader = -1;
    int i;
    for (i = 0; i < g->N; i++) {
        struct stUn_imopVarPre27 *_imopVarPre363;
        void *_imopVarPre364;
        _imopVarPre363 = &g->vertices;
        _imopVarPre364 = elem_at(_imopVarPre363, i);
        node *cur = _imopVarPre364;
        processor *p = cur->data;
        if (leader == -1) {
            leader = p->x;
        }
        if (leader != p->x) {
            ;
            disagreement = 1;
        }
    }
    if (!disagreement) {
        ;
    }
    return disagreement;
}
int main(int argc, char *argv[]) {
    int N;
    int M;
    int *xvals;
    graph *g;
    int iterate;
    int iterations = 1;
    int _imopVarPre366;
    _imopVarPre366 = input_through_argv(argc, argv);
    if ((iterate = _imopVarPre366)) {
        char *_imopVarPre368;
        struct _IO_FILE *_imopVarPre369;
        _imopVarPre368 = argv[2];
        _imopVarPre369 = fopen(_imopVarPre368, "r");
        FILE *in = _imopVarPre369;
        int *_imopVarPre371;
        _imopVarPre371 = &N;
        fscanf(in, "%d\n", _imopVarPre371);
        g = new_graph(N, 0);
        unsigned long int _imopVarPre373;
        void *_imopVarPre374;
        _imopVarPre373 = N * sizeof(int);
        _imopVarPre374 = malloc(_imopVarPre373);
        xvals = _imopVarPre374;
        int _imopVarPre375;
        _imopVarPre375 = read_graph(g, in);
        g->M = M = _imopVarPre375;
        fscanf(in, "\n");
        int i;
        for (i = 0; i < N; i++) {
            int *_imopVarPre377;
            _imopVarPre377 = &xvals[i];
            fscanf(in, "%d", _imopVarPre377);
        }
        fclose(in);
        int *_imopVarPre380;
        char *_imopVarPre381;
        _imopVarPre380 = &iterations;
        _imopVarPre381 = argv[3];
        sscanf(_imopVarPre381, "%d", _imopVarPre380);
    } else {
        N = 16;
        M = 64;
        if (argc > 1) {
            int *_imopVarPre384;
            char *_imopVarPre385;
            _imopVarPre384 = &N;
            _imopVarPre385 = argv[1];
            sscanf(_imopVarPre385, "%d", _imopVarPre384);
            int *_imopVarPre388;
            char *_imopVarPre389;
            _imopVarPre388 = &M;
            _imopVarPre389 = argv[2];
            sscanf(_imopVarPre389, "%d", _imopVarPre388);
        }
        g = generate_new_connected_graph(N, M);
        unsigned long int _imopVarPre391;
        void *_imopVarPre392;
        _imopVarPre391 = N * sizeof(int);
        _imopVarPre392 = malloc(_imopVarPre391);
        xvals = _imopVarPre392;
        int i;
        for (i = 0; i < N; i++) {
            xvals[i] = i;
        }
    }
    long long duration = 0;
    int verification;
    int i;
    int condVar_imopVarPre3;
    int something_changed_imopVarPre1;
    int i_imopVarPre2;
    int i_imopVarPre0;
    int condVar_imopVarPre4;
#pragma omp parallel
    {
#pragma omp master
        {
            i = 0;
            condVar_imopVarPre4 = (i < iterations);
        }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
        for (; condVar_imopVarPre4; ) {
#pragma omp master
            {
                begin_timer();
            }
            int something_changed = 1;
            int num_rounds = 0;
#pragma omp master
            {
                initialize_graph(g, xvals);
            }
            signed long long int _imopVarPre393;
            int v;
#pragma omp master
            {
                condVar_imopVarPre3 = something_changed;
            }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
            while (condVar_imopVarPre3) {
#pragma omp master
                {
                    something_changed_imopVarPre1 = 0;
                }
#pragma omp for nowait
                for (i_imopVarPre2 = 0; i_imopVarPre2 < g->N; i_imopVarPre2++) {
                    struct stUn_imopVarPre27 *_imopVarPre349;
                    void *_imopVarPre350;
                    _imopVarPre349 = &g->vertices;
                    _imopVarPre350 = elem_at(_imopVarPre349, i_imopVarPre2);
                    node *cur = _imopVarPre350;
                    processor *p = cur->data;
                    int new_x = p->x;
                    int j;
                    for (j = 0; j < cur->degree; j++) {
                        struct stUn_imopVarPre27 *_imopVarPre357;
                        void *_imopVarPre358;
                        _imopVarPre357 = &cur->neighbors;
                        _imopVarPre358 = elem_at(_imopVarPre357, j);
                        node *neighbor = *((node **) _imopVarPre358);
                        processor *neighbor_p = neighbor->data;
                        if (new_x < neighbor_p->x) {
                            new_x = neighbor_p->x;
                        }
                    }
                    p->new_x = new_x;
                }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp for nowait
                for (i_imopVarPre0 = 0; i_imopVarPre0 < g->N; i_imopVarPre0++) {
                    struct stUn_imopVarPre27 *_imopVarPre360;
                    void *_imopVarPre361;
                    _imopVarPre360 = &g->vertices;
                    _imopVarPre361 = elem_at(_imopVarPre360, i_imopVarPre0);
                    node *cur = _imopVarPre361;
                    processor *p = cur->data;
                    if (p->new_x != p->x) {
                        something_changed_imopVarPre1 = 1;
                    }
                    p->x = p->new_x;
                }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp master
                {
                    something_changed_imopVarPre1 = something_changed_imopVarPre1;
                    num_rounds++;
                }
#pragma omp master
                {
                    condVar_imopVarPre3 = something_changed;
                }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
            }
#pragma omp master
            {
                _imopVarPre393 = time_elapsed();
                duration += _imopVarPre393;
                verification = verify_and_print_solution(g);
                for (v = 0; v < g->N; v++) {
                    struct stUn_imopVarPre27 *_imopVarPre395;
                    void *_imopVarPre396;
                    _imopVarPre395 = &g->vertices;
                    _imopVarPre396 = elem_at(_imopVarPre395, v);
                    node *cur = _imopVarPre396;
                    void *_imopVarPre398;
                    _imopVarPre398 = cur->data;
                    free(_imopVarPre398);
                }
            }
#pragma omp master
            {
                i++;
                condVar_imopVarPre4 = (i < iterations);
            }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
        }
    }
    free(xvals);
    if (iterate) {
        double _imopVarPre400;
        _imopVarPre400 = ((double) duration) / iterations;
        printf("%.2lf\n", _imopVarPre400);
    }
    return verification;
}
