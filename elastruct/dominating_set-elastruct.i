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
extern void *memset(void *__s, int __c , size_t __n);
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
    int color;
    vector W;
    int w;
    int w_tilde;
    int w_hat;
    int active;
    int s;
    int s_hat;
    int candidate;
    int c;
    int joined;
    vector n2;
} ;
typedef struct stUn_imopVarPre32 payload;
int ceil_power_of_2(int x) {
    if ((x & (x - 1)) == 0) {
        return x;
    }
    int r = 1;
    int p = 1;
    while (r < x) {
        p = r;
        r <<= 1;
    }
    return p;
}
int verify_and_print_solution(graph *g) {
    ;
    int i;
    for (i = 0; i < g->N; i++) {
        struct stUn_imopVarPre27 *_imopVarPre494;
        void *_imopVarPre495;
        _imopVarPre494 = &g->vertices;
        _imopVarPre495 = elem_at(_imopVarPre494, i);
        node *v = _imopVarPre495;
        payload *v_data = v->data;
        if (!v_data->joined) {
            continue;
        }
        ;
    }
    ;
    ;
    return 0;
}
int main(int argc, char *argv[]) {
    int N;
    int M;
    graph *g;
    int iterate;
    int iterations = 1;
    int _imopVarPre497;
    _imopVarPre497 = input_through_argv(argc, argv);
    if ((iterate = _imopVarPre497)) {
        char *_imopVarPre499;
        struct _IO_FILE *_imopVarPre500;
        _imopVarPre499 = argv[2];
        _imopVarPre500 = fopen(_imopVarPre499, "r");
        FILE *in = _imopVarPre500;
        int *_imopVarPre502;
        _imopVarPre502 = &N;
        fscanf(in, "%d\n", _imopVarPre502);
        g = new_graph(N, 0);
        int _imopVarPre503;
        _imopVarPre503 = read_graph(g, in);
        g->M = M = _imopVarPre503;
        fclose(in);
        int *_imopVarPre506;
        char *_imopVarPre507;
        _imopVarPre506 = &iterations;
        _imopVarPre507 = argv[3];
        sscanf(_imopVarPre507, "%d", _imopVarPre506);
    } else {
        N = 16;
        M = 64;
        if (argc > 1) {
            int *_imopVarPre510;
            char *_imopVarPre511;
            _imopVarPre510 = &N;
            _imopVarPre511 = argv[1];
            sscanf(_imopVarPre511, "%d", _imopVarPre510);
            int *_imopVarPre514;
            char *_imopVarPre515;
            _imopVarPre514 = &M;
            _imopVarPre515 = argv[2];
            sscanf(_imopVarPre515, "%d", _imopVarPre514);
        }
        g = generate_new_connected_graph(N, M);
    }
    long long duration = 0;
    int verification;
    int i;
    int i_imopVarPre12;
    int _imopVarPre516;
    int i_imopVarPre8;
    int i_imopVarPre3;
    int i_imopVarPre5;
    int i_imopVarPre10;
    int i_imopVarPre7;
    int i_imopVarPre11;
    int i_imopVarPre9;
    int i_imopVarPre0;
    int i_imopVarPre2;
    int i_imopVarPre6;
    int i_imopVarPre1;
    int i_imopVarPre4;
    int condVar_imopVarPre13;
    for (i = 0; i < iterations; i++) {
#pragma omp parallel
        {
#pragma omp master
            {
                result = 0;
                begin_timer();
            }
#pragma omp for nowait
            for (i_imopVarPre12 = 0; i_imopVarPre12 < g->N; i_imopVarPre12++) {
                struct stUn_imopVarPre27 *_imopVarPre343;
                void *_imopVarPre344;
                _imopVarPre343 = &g->vertices;
                _imopVarPre344 = elem_at(_imopVarPre343, i_imopVarPre12);
                node *v = _imopVarPre344;
                unsigned long int _imopVarPre346;
                void *_imopVarPre347;
                _imopVarPre346 = sizeof(payload);
                _imopVarPre347 = malloc(_imopVarPre346);
                payload *data = _imopVarPre347;
                data->color = 0;
                data->joined = 0;
                unsigned long int _imopVarPre350;
                struct stUn_imopVarPre27 *_imopVarPre351;
                _imopVarPre350 = sizeof(node *);
                _imopVarPre351 = &data->W;
                initialize_vector(_imopVarPre351, _imopVarPre350);
                unsigned long int _imopVarPre353;
                void *_imopVarPre354;
                _imopVarPre353 = g->N * sizeof(int);
                _imopVarPre354 = malloc(_imopVarPre353);
                int *visited = _imopVarPre354;
                unsigned long int _imopVarPre356;
                _imopVarPre356 = g->N * sizeof(int);
                memset(visited, 0, _imopVarPre356);
                unsigned long int _imopVarPre359;
                struct stUn_imopVarPre27 *_imopVarPre360;
                _imopVarPre359 = sizeof(node *);
                _imopVarPre360 = &data->n2;
                initialize_vector(_imopVarPre360, _imopVarPre359);
                int j;
                for (j = 0; j < v->degree; j++) {
                    struct stUn_imopVarPre27 *_imopVarPre367;
                    void *_imopVarPre368;
                    _imopVarPre367 = &v->neighbors;
                    _imopVarPre368 = elem_at(_imopVarPre367, j);
                    node *u = *((node **) _imopVarPre368);
                    if (visited[u->label]) {
                        continue;
                    }
                    visited[u->label] = 1;
                    struct stUn_imopVarPre28 **_imopVarPre371;
                    struct stUn_imopVarPre27 *_imopVarPre372;
                    _imopVarPre371 = &u;
                    _imopVarPre372 = &data->n2;
                    append_to_vector(_imopVarPre372, _imopVarPre371);
                    int k;
                    for (k = 0; k < u->degree; k++) {
                        struct stUn_imopVarPre27 *_imopVarPre379;
                        void *_imopVarPre380;
                        _imopVarPre379 = &u->neighbors;
                        _imopVarPre380 = elem_at(_imopVarPre379, k);
                        node *w = *((node **) _imopVarPre380);
                        if (w == v) {
                            continue;
                        }
                        if (visited[w->label]) {
                            continue;
                        }
                        visited[w->label] = 1;
                        struct stUn_imopVarPre28 **_imopVarPre383;
                        struct stUn_imopVarPre27 *_imopVarPre384;
                        _imopVarPre383 = &w;
                        _imopVarPre384 = &data->n2;
                        append_to_vector(_imopVarPre384, _imopVarPre383);
                    }
                }
                struct stUn_imopVarPre28 **_imopVarPre387;
                struct stUn_imopVarPre27 *_imopVarPre388;
                _imopVarPre387 = &v;
                _imopVarPre388 = &data->n2;
                append_to_vector(_imopVarPre388, _imopVarPre387);
                free(visited);
                v->data = data;
            }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp for nowait
            for (i_imopVarPre8 = 0; i_imopVarPre8 < g->N; i_imopVarPre8++) {
                struct stUn_imopVarPre27 *_imopVarPre390;
                void *_imopVarPre391;
                _imopVarPre390 = &g->vertices;
                _imopVarPre391 = elem_at(_imopVarPre390, i_imopVarPre8);
                node *v = _imopVarPre391;
                payload *data = v->data;
                if (data->color == 0) {
                    ;
                    result = 1;
                }
            }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp master
            {
                _imopVarPre516 = result;
            }
        }
        /**/
        int result;
#pragma omp parallel
        {
            signed long long int _imopVarPre517;
#pragma omp master
            {
                condVar_imopVarPre13 = _imopVarPre516;
            }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
            while (condVar_imopVarPre13) {
#pragma omp master
                {
                    result = 0;
                    ;
                }
#pragma omp for nowait
                for (i_imopVarPre3 = 0; i_imopVarPre3 < g->N; i_imopVarPre3++) {
                    struct stUn_imopVarPre27 *_imopVarPre393;
                    void *_imopVarPre394;
                    _imopVarPre393 = &g->vertices;
                    _imopVarPre394 = elem_at(_imopVarPre393, i_imopVarPre3);
                    node *v = _imopVarPre394;
                    payload *v_data = v->data;
                    v_data->W.used = 0;
                    if (v_data->color == 0) {
                        struct stUn_imopVarPre28 **_imopVarPre397;
                        struct stUn_imopVarPre27 *_imopVarPre398;
                        _imopVarPre397 = &v;
                        _imopVarPre398 = &v_data->W;
                        append_to_vector(_imopVarPre398, _imopVarPre397);
                    }
                    int j;
                    for (j = 0; j < v->degree; j++) {
                        struct stUn_imopVarPre27 *_imopVarPre405;
                        void *_imopVarPre406;
                        _imopVarPre405 = &v->neighbors;
                        _imopVarPre406 = elem_at(_imopVarPre405, j);
                        node *u = *((node **) _imopVarPre406);
                        payload *u_data = u->data;
                        if (u_data->color == 0) {
                            struct stUn_imopVarPre28 **_imopVarPre409;
                            struct stUn_imopVarPre27 *_imopVarPre410;
                            _imopVarPre409 = &u;
                            _imopVarPre410 = &v_data->W;
                            append_to_vector(_imopVarPre410, _imopVarPre409);
                        }
                    }
                    v_data->w = v_data->W.used;
                    ;
                }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp master
                {
                    ;
                }
#pragma omp for nowait
                for (i_imopVarPre5 = 0; i_imopVarPre5 < g->N; i_imopVarPre5++) {
                    struct stUn_imopVarPre27 *_imopVarPre412;
                    void *_imopVarPre413;
                    _imopVarPre412 = &g->vertices;
                    _imopVarPre413 = elem_at(_imopVarPre412, i_imopVarPre5);
                    node *v = _imopVarPre413;
                    payload *v_data = v->data;
                    int _imopVarPre415;
                    int _imopVarPre416;
                    _imopVarPre415 = v_data->w;
                    _imopVarPre416 = ceil_power_of_2(_imopVarPre415);
                    v_data->w_tilde = _imopVarPre416;
                    ;
                }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp master
                {
                    ;
                }
#pragma omp for nowait
                for (i_imopVarPre10 = 0; i_imopVarPre10 < g->N; i_imopVarPre10++) {
                    struct stUn_imopVarPre27 *_imopVarPre418;
                    void *_imopVarPre419;
                    _imopVarPre418 = &g->vertices;
                    _imopVarPre419 = elem_at(_imopVarPre418, i_imopVarPre10);
                    node *v = _imopVarPre419;
                    payload *v_data = v->data;
                    if (v_data->W.used == 0) {
                        continue;
                    }
                    int w_hat = 0;
                    int j;
                    for (j = 0; j < v_data->n2.used; j++) {
                        struct stUn_imopVarPre27 *_imopVarPre426;
                        void *_imopVarPre427;
                        _imopVarPre426 = &v_data->n2;
                        _imopVarPre427 = elem_at(_imopVarPre426, j);
                        node *u = *((node **) _imopVarPre427);
                        payload *u_data = u->data;
                        if (u_data->w_tilde > w_hat) {
                            w_hat = u_data->w_tilde;
                        }
                    }
                    v_data->w_hat = w_hat;
                    ;
                }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp master
                {
                    ;
                }
#pragma omp for nowait
                for (i_imopVarPre7 = 0; i_imopVarPre7 < g->N; i_imopVarPre7++) {
                    struct stUn_imopVarPre27 *_imopVarPre429;
                    void *_imopVarPre430;
                    _imopVarPre429 = &g->vertices;
                    _imopVarPre430 = elem_at(_imopVarPre429, i_imopVarPre7);
                    node *v = _imopVarPre430;
                    payload *v_data = v->data;
                    if (v_data->W.used == 0) {
                        continue;
                    }
                    if (v_data->w_hat == v_data->w_tilde) {
                        v_data->active = 1;
                    } else {
                        v_data->active = 0;
                    }
                    ;
                }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp master
                {
                    ;
                }
#pragma omp for nowait
                for (i_imopVarPre11 = 0; i_imopVarPre11 < g->N; i_imopVarPre11++) {
                    struct stUn_imopVarPre27 *_imopVarPre432;
                    void *_imopVarPre433;
                    _imopVarPre432 = &g->vertices;
                    _imopVarPre433 = elem_at(_imopVarPre432, i_imopVarPre11);
                    node *v = _imopVarPre433;
                    payload *v_data = v->data;
                    if (v_data->W.used == 0) {
                        continue;
                    }
                    int support = v_data->active;
                    int j;
                    for (j = 0; j < v->degree; j++) {
                        struct stUn_imopVarPre27 *_imopVarPre440;
                        void *_imopVarPre441;
                        _imopVarPre440 = &v->neighbors;
                        _imopVarPre441 = elem_at(_imopVarPre440, j);
                        node *u = *((node **) _imopVarPre441);
                        payload *u_data = u->data;
                        if (u_data->active) {
                            support++;
                        }
                    }
                    ;
                    v_data->s = support;
                }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp master
                {
                    ;
                }
#pragma omp for nowait
                for (i_imopVarPre9 = 0; i_imopVarPre9 < g->N; i_imopVarPre9++) {
                    struct stUn_imopVarPre27 *_imopVarPre443;
                    void *_imopVarPre444;
                    _imopVarPre443 = &g->vertices;
                    _imopVarPre444 = elem_at(_imopVarPre443, i_imopVarPre9);
                    node *v = _imopVarPre444;
                    payload *v_data = v->data;
                    if (v_data->W.used == 0) {
                        continue;
                    }
                    int s_hat = 0;
                    int j;
                    for (j = 0; j < v_data->W.used; j++) {
                        struct stUn_imopVarPre27 *_imopVarPre451;
                        void *_imopVarPre452;
                        _imopVarPre451 = &v_data->W;
                        _imopVarPre452 = elem_at(_imopVarPre451, j);
                        node *u = *((node **) _imopVarPre452);
                        payload *u_data = u->data;
                        if (u_data->s > s_hat) {
                            s_hat = u_data->s;
                        }
                    }
                    ;
                    v_data->s_hat = s_hat;
                }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp master
                {
                    ;
                }
#pragma omp for nowait
                for (i_imopVarPre0 = 0; i_imopVarPre0 < g->N; i_imopVarPre0++) {
                    struct stUn_imopVarPre27 *_imopVarPre454;
                    void *_imopVarPre455;
                    _imopVarPre454 = &g->vertices;
                    _imopVarPre455 = elem_at(_imopVarPre454, i_imopVarPre0);
                    node *v = _imopVarPre455;
                    payload *v_data = v->data;
                    if (v_data->W.used == 0) {
                        continue;
                    }
                    v_data->candidate = 0;
                    if (v_data->active) {
                        int _imopVarPre457;
                        _imopVarPre457 = rand();
                        int r = _imopVarPre457 % (v_data->s_hat);
                        if (r == 0) {
                            v_data->candidate = 1;
                        }
                    }
                    ;
                }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp master
                {
                    ;
                }
#pragma omp for nowait
                for (i_imopVarPre2 = 0; i_imopVarPre2 < g->N; i_imopVarPre2++) {
                    struct stUn_imopVarPre27 *_imopVarPre459;
                    void *_imopVarPre460;
                    _imopVarPre459 = &g->vertices;
                    _imopVarPre460 = elem_at(_imopVarPre459, i_imopVarPre2);
                    node *v = _imopVarPre460;
                    payload *v_data = v->data;
                    if (v_data->W.used == 0) {
                        continue;
                    }
                    v_data->c = 0;
                    int j;
                    for (j = 0; j < v_data->W.used; j++) {
                        struct stUn_imopVarPre27 *_imopVarPre467;
                        void *_imopVarPre468;
                        _imopVarPre467 = &v_data->W;
                        _imopVarPre468 = elem_at(_imopVarPre467, j);
                        node *u = *((node **) _imopVarPre468);
                        payload *u_data = u->data;
                        if (u_data->candidate) {
                            v_data->c++;
                        }
                    }
                    ;
                }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp master
                {
                    ;
                }
#pragma omp for nowait
                for (i_imopVarPre6 = 0; i_imopVarPre6 < g->N; i_imopVarPre6++) {
                    struct stUn_imopVarPre27 *_imopVarPre470;
                    void *_imopVarPre471;
                    _imopVarPre470 = &g->vertices;
                    _imopVarPre471 = elem_at(_imopVarPre470, i_imopVarPre6);
                    node *v = _imopVarPre471;
                    payload *v_data = v->data;
                    if (v_data->W.used == 0) {
                        continue;
                    }
                    int sigma_c = 0;
                    int j;
                    for (j = 0; j < v_data->W.used; j++) {
                        struct stUn_imopVarPre27 *_imopVarPre478;
                        void *_imopVarPre479;
                        _imopVarPre478 = &v_data->W;
                        _imopVarPre479 = elem_at(_imopVarPre478, j);
                        node *u = *((node **) _imopVarPre479);
                        payload *u_data = u->data;
                        sigma_c += u_data->c;
                    }
                    int _imopVarPre481;
                    _imopVarPre481 = v_data->candidate;
                    if (_imopVarPre481) {
                        _imopVarPre481 = sigma_c <= 3 * v_data->w;
                    }
                    if (_imopVarPre481) {
                        ;
                        v_data->color = 2;
                        v_data->joined = 1;
                    }
                }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp master
                {
                    ;
                }
#pragma omp for nowait
                for (i_imopVarPre1 = 0; i_imopVarPre1 < g->N; i_imopVarPre1++) {
                    struct stUn_imopVarPre27 *_imopVarPre483;
                    void *_imopVarPre484;
                    _imopVarPre483 = &g->vertices;
                    _imopVarPre484 = elem_at(_imopVarPre483, i_imopVarPre1);
                    node *v = _imopVarPre484;
                    payload *v_data = v->data;
                    if (v_data->color != 0) {
                        continue;
                    }
                    int j;
                    for (j = 0; j < v->degree; j++) {
                        struct stUn_imopVarPre27 *_imopVarPre491;
                        void *_imopVarPre492;
                        _imopVarPre491 = &v->neighbors;
                        _imopVarPre492 = elem_at(_imopVarPre491, j);
                        node *u = *((node **) _imopVarPre492);
                        payload *u_data = u->data;
                        if (u_data->color == 2) {
                            v_data->color = 1;
                            break;
                        }
                    }
                }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp for nowait
                for (i_imopVarPre4 = 0; i_imopVarPre4 < g->N; i_imopVarPre4++) {
                    struct stUn_imopVarPre27 *_imopVarPre390;
                    void *_imopVarPre391;
                    _imopVarPre390 = &g->vertices;
                    _imopVarPre391 = elem_at(_imopVarPre390, i_imopVarPre4);
                    node *v = _imopVarPre391;
                    payload *data = v->data;
                    if (data->color == 0) {
                        ;
                        result = 1;
                    }
                }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp master
                {
                    _imopVarPre516 = result;
                }
#pragma omp master
                {
                    condVar_imopVarPre13 = _imopVarPre516;
                }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
            }
#pragma omp master
            {
                _imopVarPre517 = time_elapsed();
                duration += _imopVarPre517;
                verification = verify_and_print_solution(g);
            }
        }
    }
    if (iterate) {
        double _imopVarPre519;
        _imopVarPre519 = ((double) duration) / iterations;
        printf("%.2lf\n", _imopVarPre519);
    }
    return verification;
}
