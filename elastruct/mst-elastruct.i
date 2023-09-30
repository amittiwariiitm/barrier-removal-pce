typedef long int __off_t;
typedef long int __off64_t;
typedef long int __time_t;
typedef int __clockid_t;
typedef long int __syscall_slong_t;
struct timespec {
    __time_t tv_sec;
    __syscall_slong_t tv_nsec;
} ;
struct stUn_imopVarPre4 {
    unsigned char _x[4];
} ;
typedef struct stUn_imopVarPre4 omp_lock_t;
extern void omp_init_lock(omp_lock_t *);
extern void omp_destroy_lock(omp_lock_t *);
extern void omp_set_lock(omp_lock_t *);
extern void omp_unset_lock(omp_lock_t *);
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
void read_weights(graph *, FILE *);
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
    int _imopVarPre115;
    char *_imopVarPre116;
    int _imopVarPre117;
    _imopVarPre115 = argc == 4;
    if (_imopVarPre115) {
        _imopVarPre116 = argv[1];
        _imopVarPre117 = strcmp(_imopVarPre116, "-");
        _imopVarPre115 = _imopVarPre117 == 0;
    }
    if (_imopVarPre115) {
        return 2;
    }
    return 0;
}
void begin_timer() {
    struct timespec *_imopVarPre119;
    _imopVarPre119 = &start_time;
    clock_gettime(2, _imopVarPre119);
}
long long time_elapsed() {
    struct timespec end_time;
    struct timespec *_imopVarPre121;
    _imopVarPre121 = &end_time;
    clock_gettime(2, _imopVarPre121);
    long long int s = end_time.tv_sec - start_time.tv_sec;
    long long int ns = end_time.tv_nsec - start_time.tv_nsec;
    return (s * ((long long) 1e9)) + ns;
}
void initialize_vector(vector *v, size_t object_size) {
    v->object_size = object_size;
    v->allocated = 16;
    unsigned long int _imopVarPre123;
    void *_imopVarPre124;
    _imopVarPre123 = v->allocated * v->object_size;
    _imopVarPre124 = malloc(_imopVarPre123);
    v->items = _imopVarPre124;
    v->used = 0;
}
vector *new_vector(size_t object_size) {
    unsigned long int _imopVarPre127;
    void *_imopVarPre128;
    _imopVarPre127 = 1 * sizeof(vector);
    _imopVarPre128 = malloc(_imopVarPre127);
    vector *v = (vector *) _imopVarPre128;
    initialize_vector(v, object_size);
    return v;
}
void append_to_vector(vector *v, void *object) {
    while (v->allocated <= v->used + 1) {
        v->allocated *= 2;
        unsigned long int _imopVarPre131;
        void *_imopVarPre132;
        void *_imopVarPre133;
        _imopVarPre131 = v->allocated * v->object_size;
        _imopVarPre132 = v->items;
        _imopVarPre133 = realloc(_imopVarPre132, _imopVarPre131);
        v->items = _imopVarPre133;
    }
    unsigned long int _imopVarPre136;
    void *_imopVarPre137;
    _imopVarPre136 = v->object_size;
    _imopVarPre137 = v->items + (v->used * v->object_size);
    memcpy(_imopVarPre137, object, _imopVarPre136);
    v->used++;
}
void *elem_at(vector *v, size_t idx) {
    return v->items + (idx * v->object_size);
}
void free_vector(vector *v) {
    void *_imopVarPre139;
    _imopVarPre139 = v->items;
    free(_imopVarPre139);
    free(v);
}
graph *new_graph(int N, int M) {
    unsigned long int _imopVarPre141;
    void *_imopVarPre142;
    _imopVarPre141 = 1 * sizeof(graph);
    _imopVarPre142 = malloc(_imopVarPre141);
    graph *g = _imopVarPre142;
    g->N = N;
    g->M = M;
    unsigned long int _imopVarPre145;
    struct stUn_imopVarPre27 *_imopVarPre146;
    _imopVarPre145 = sizeof(node);
    _imopVarPre146 = &g->vertices;
    initialize_vector(_imopVarPre146, _imopVarPre145);
    int i;
    for (i = 0; i < g->N; i++) {
        node u;
        unsigned long int _imopVarPre149;
        struct stUn_imopVarPre27 *_imopVarPre150;
        _imopVarPre149 = sizeof(node *);
        _imopVarPre150 = &u.neighbors;
        initialize_vector(_imopVarPre150, _imopVarPre149);
        u.degree = 0;
        u.data = ((void *) 0);
        u.label = i;
        u.weight = 0;
        struct stUn_imopVarPre28 *_imopVarPre153;
        struct stUn_imopVarPre27 *_imopVarPre154;
        _imopVarPre153 = &u;
        _imopVarPre154 = &g->vertices;
        append_to_vector(_imopVarPre154, _imopVarPre153);
    }
    unsigned long int _imopVarPre157;
    void *_imopVarPre158;
    _imopVarPre157 = N * sizeof(int *);
    _imopVarPre158 = malloc(_imopVarPre157);
    g->adj_mat = (int **) _imopVarPre158;
    for (i = 0; i < g->N; i++) {
        unsigned long int _imopVarPre161;
        void *_imopVarPre162;
        _imopVarPre161 = sizeof(int);
        _imopVarPre162 = calloc(N, _imopVarPre161);
        g->adj_mat[i] = (int *) _imopVarPre162;
    }
    g->root = ((void *) 0);
    ;
    return g;
}
int read_graph(graph *g, FILE *in) {
    int M = 0;
    int _imopVarPre164;
    void *_imopVarPre165;
    _imopVarPre164 = g->N + 2;
    _imopVarPre165 = malloc(_imopVarPre164);
    char *line = _imopVarPre165;
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
void read_weights(graph *g, FILE *in) {
    int i;
    for (i = 0; i < g->N; i++) {
        int j;
        for (j = 0; j < g->N; j++) {
            int x;
            int *_imopVarPre167;
            _imopVarPre167 = &x;
            fscanf(in, "%d", _imopVarPre167);
            if (i == j) {
                continue;
            }
            if (g->adj_mat[j][i]) {
                g->adj_mat[i][j] = x;
            }
        }
    }
}
void add_edge(graph *g, int i , int j) {
    struct stUn_imopVarPre27 *_imopVarPre171;
    void *_imopVarPre172;
    _imopVarPre171 = &g->vertices;
    _imopVarPre172 = elem_at(_imopVarPre171, i);
    node *node_u = _imopVarPre172;
    struct stUn_imopVarPre27 *_imopVarPre174;
    void *_imopVarPre175;
    _imopVarPre174 = &g->vertices;
    _imopVarPre175 = elem_at(_imopVarPre174, j);
    node *node_v = _imopVarPre175;
    struct stUn_imopVarPre28 **_imopVarPre178;
    struct stUn_imopVarPre27 *_imopVarPre179;
    _imopVarPre178 = &node_v;
    _imopVarPre179 = &node_u->neighbors;
    append_to_vector(_imopVarPre179, _imopVarPre178);
    struct stUn_imopVarPre28 **_imopVarPre182;
    struct stUn_imopVarPre27 *_imopVarPre183;
    _imopVarPre182 = &node_u;
    _imopVarPre183 = &node_v->neighbors;
    append_to_vector(_imopVarPre183, _imopVarPre182);
    node_u->degree++;
    node_v->degree++;
    g->adj_mat[i][j] = g->adj_mat[j][i] = 1;
}
void free_graph(graph *g) {
    ;
    if (g->adj_mat != ((void *) 0)) {
        int i;
        for (i = 0; i < g->N; i++) {
            int *_imopVarPre185;
            _imopVarPre185 = g->adj_mat[i];
            free(_imopVarPre185);
        }
        int **_imopVarPre187;
        _imopVarPre187 = g->adj_mat;
        free(_imopVarPre187);
    }
}
int is_connected(graph *g) {
    unsigned long int _imopVarPre190;
    int _imopVarPre191;
    void *_imopVarPre192;
    _imopVarPre190 = sizeof(int);
    _imopVarPre191 = g->N;
    _imopVarPre192 = calloc(_imopVarPre191, _imopVarPre190);
    int *visited = _imopVarPre192;
    struct stUn_imopVarPre27 *_imopVarPre194;
    void *_imopVarPre195;
    _imopVarPre194 = &g->vertices;
    _imopVarPre195 = elem_at(_imopVarPre194, 0);
    node *root = _imopVarPre195;
    visited[root->label] = 1;
    unsigned long int _imopVarPre197;
    struct stUn_imopVarPre27 *_imopVarPre198;
    _imopVarPre197 = sizeof(node *);
    _imopVarPre198 = new_vector(_imopVarPre197);
    vector *queue = _imopVarPre198;
    struct stUn_imopVarPre28 **_imopVarPre200;
    _imopVarPre200 = &root;
    append_to_vector(queue, _imopVarPre200);
    int queue_position = 0;
    ;
    while (queue_position < queue->used) {
        void *_imopVarPre204;
        _imopVarPre204 = elem_at(queue, queue_position);
        node *cur = *((node **) _imopVarPre204);
        queue_position++;
        int i;
        for (i = 0; i < cur->degree; i++) {
            struct stUn_imopVarPre27 *_imopVarPre211;
            void *_imopVarPre212;
            _imopVarPre211 = &cur->neighbors;
            _imopVarPre212 = elem_at(_imopVarPre211, i);
            node *neighbor = *((node **) _imopVarPre212);
            if (visited[neighbor->label] > 0) {
                continue;
            }
            visited[neighbor->label] = 1;
            struct stUn_imopVarPre28 **_imopVarPre214;
            _imopVarPre214 = &neighbor;
            append_to_vector(queue, _imopVarPre214);
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
    struct stUn_imopVarPre29 *_imopVarPre215;
    _imopVarPre215 = new_graph(N, M);
    graph *g = _imopVarPre215;
    ;
    int edges_created = 0;
    while (edges_created < M) {
        int _imopVarPre217;
        _imopVarPre217 = rand();
        int u = _imopVarPre217 % N;
        int _imopVarPre219;
        _imopVarPre219 = rand();
        int v = _imopVarPre219 % N;
        if (u == v) {
            continue;
        }
        if (u > v) {
            int *_imopVarPre222;
            int *_imopVarPre223;
            _imopVarPre222 = &v;
            _imopVarPre223 = &u;
            swap(_imopVarPre223, _imopVarPre222);
        }
        if (g->adj_mat[u][v] != 0) {
            continue;
        }
        int _imopVarPre225;
        _imopVarPre225 = rand();
        g->adj_mat[u][v] = g->adj_mat[v][u] = 1 + _imopVarPre225 % 100;
        add_edge(g, u, v);
        edges_created += 1;
    }
    ;
    return g;
}
graph *generate_new_connected_graph(int N, int M) {
    graph *g = ((void *) 0);
    int attempts = 0;
    int _imopVarPre227;
    do {
        attempts++;
        if (g != ((void *) 0)) {
            free_graph(g);
        }
        ;
        g = generate_new_graph(N, M);
        _imopVarPre227 = is_connected(g);
    } while (!_imopVarPre227);
    ;
    return g;
}
omp_lock_t *new_locks(int );
void free_locks(omp_lock_t *, int );
omp_lock_t *new_locks(int N) {
    ;
    unsigned long int _imopVarPre304;
    void *_imopVarPre305;
    _imopVarPre304 = N * sizeof(omp_lock_t);
    _imopVarPre305 = malloc(_imopVarPre304);
    omp_lock_t *locks = _imopVarPre305;
    ;
    int i;
    for (i = 0; i < N; i++) {
        struct stUn_imopVarPre4 *_imopVarPre307;
        _imopVarPre307 = locks + i;
        omp_init_lock(_imopVarPre307);
// #pragma omp dummyFlush LOCK_WRITE_END
    }
    return locks;
}
void free_locks(omp_lock_t *locks, int N) {
    ;
    int i;
    for (i = 0; i < N; i++) {
        struct stUn_imopVarPre4 *_imopVarPre309;
        _imopVarPre309 = locks + i;
        omp_destroy_lock(_imopVarPre309);
    }
    ;
    free(locks);
}
struct stUn_imopVarPre31 {
    int N;
    vector **queues;
    int *front;
    omp_lock_t *locks;
} ;
typedef struct stUn_imopVarPre31 queuelist;
queuelist *new_queuelist(int , size_t );
void free_queuelist(queuelist *);
void enqueue(queuelist *, int  , void *);
void *dequeue(queuelist *, int );
int is_ql_queue_empty(queuelist *, int );
queuelist *new_queuelist(int N, size_t elem_size) {
    unsigned long int _imopVarPre311;
    void *_imopVarPre312;
    _imopVarPre311 = sizeof(queuelist);
    _imopVarPre312 = malloc(_imopVarPre311);
    queuelist *ql = _imopVarPre312;
    ql->N = N;
    ;
    unsigned long int _imopVarPre314;
    void *_imopVarPre315;
    _imopVarPre314 = N * sizeof(vector *);
    _imopVarPre315 = malloc(_imopVarPre314);
    vector **queues = _imopVarPre315;
    ;
    int i;
    for (i = 0; i < N; i++) {
        struct stUn_imopVarPre27 *_imopVarPre316;
        _imopVarPre316 = new_vector(elem_size);
        queues[i] = _imopVarPre316;
    }
    ql->queues = queues;
    unsigned long int _imopVarPre318;
    void *_imopVarPre319;
    _imopVarPre318 = N * sizeof(int);
    _imopVarPre319 = malloc(_imopVarPre318);
    ql->front = _imopVarPre319;
    for (i = 0; i < N; i++) {
        ql->front[i] = 0;
    }
    struct stUn_imopVarPre4 *_imopVarPre320;
    _imopVarPre320 = new_locks(N);
    ql->locks = _imopVarPre320;
    return ql;
}
void free_queuelist(queuelist *ql) {
    int N = ql->N;
    ;
    int i;
    for (i = 0; i < N; i++) {
        struct stUn_imopVarPre27 *_imopVarPre322;
        _imopVarPre322 = ql->queues[i];
        free_vector(_imopVarPre322);
    }
    ;
    int *_imopVarPre324;
    _imopVarPre324 = ql->front;
    free(_imopVarPre324);
    ;
    struct stUn_imopVarPre4 *_imopVarPre326;
    _imopVarPre326 = ql->locks;
    free_locks(_imopVarPre326, N);
    ;
    free(ql);
}
void enqueue(queuelist *ql, int i , void *object) {
    struct stUn_imopVarPre4 *_imopVarPre328;
    _imopVarPre328 = ql->locks + i;
// #pragma omp dummyFlush LOCK_MODIFY_START
    omp_set_lock(_imopVarPre328);
// #pragma omp dummyFlush LOCK_MODIFY_END
    struct stUn_imopVarPre27 *_imopVarPre330;
    _imopVarPre330 = ql->queues[i];
    append_to_vector(_imopVarPre330, object);
    struct stUn_imopVarPre4 *_imopVarPre332;
    _imopVarPre332 = ql->locks + i;
    omp_unset_lock(_imopVarPre332);
// #pragma omp dummyFlush LOCK_WRITE_END
}
void *dequeue(queuelist *ql, int i) {
    struct stUn_imopVarPre4 *_imopVarPre334;
    _imopVarPre334 = ql->locks + i;
// #pragma omp dummyFlush LOCK_MODIFY_START
    omp_set_lock(_imopVarPre334);
// #pragma omp dummyFlush LOCK_MODIFY_END
    void *object;
    vector *v = ql->queues[i];
    if (ql->front[i] == v->used) {
        object = ((void *) 0);
        goto end;
    }
    int _imopVarPre336;
    void *_imopVarPre337;
    _imopVarPre336 = ql->front[i];
    _imopVarPre337 = elem_at(v, _imopVarPre336);
    object = _imopVarPre337;
    ql->front[i]++;
    if (ql->front[i] == v->used) {
        ql->front[i] = 0;
        v->used = 0;
    }
    struct stUn_imopVarPre4 *_imopVarPre339;
    end: _imopVarPre339 = ql->locks + i;
    omp_unset_lock(_imopVarPre339);
// #pragma omp dummyFlush LOCK_WRITE_END
    return object;
}
int is_ql_queue_empty(queuelist *ql, int i) {
    struct stUn_imopVarPre4 *_imopVarPre341;
    _imopVarPre341 = ql->locks + i;
// #pragma omp dummyFlush LOCK_MODIFY_START
    omp_set_lock(_imopVarPre341);
// #pragma omp dummyFlush LOCK_MODIFY_END
    vector *v = ql->queues[i];
    int result = ql->front[i] == v->used;
    struct stUn_imopVarPre4 *_imopVarPre343;
    _imopVarPre343 = ql->locks + i;
    omp_unset_lock(_imopVarPre343);
// #pragma omp dummyFlush LOCK_WRITE_END
    return result;
}
struct stUn_imopVarPre32 {
    int from;
} ;
typedef struct stUn_imopVarPre32 message;
struct stUn_imopVarPre33 {
    int u;
    int v;
    int w;
} ;
typedef struct stUn_imopVarPre33 edge;
struct stUn_imopVarPre34 {
    int fragment_id;
    int tmp_fragment_id;
    int received_first_message;
    edge *b;
} ;
typedef struct stUn_imopVarPre34 payload;
int multiple_fragments(graph *g) {
    int multiple = 0;
    int last = -1;
    ;
    int i;
    for (i = 0; i < g->N; i++) {
        struct stUn_imopVarPre27 *_imopVarPre351;
        void *_imopVarPre352;
        _imopVarPre351 = &g->vertices;
        _imopVarPre352 = elem_at(_imopVarPre351, i);
        node *u = _imopVarPre352;
        payload *u_data = u->data;
        if (last == -1) {
            last = u_data->fragment_id;
        } else {
            if (u_data->fragment_id != last) {
                multiple = 1;
                break;
            }
        }
    }
    return multiple;
}
void change_fragment(graph *g, int from , int to) {
    ;
    int i;
    for (i = 0; i < g->N; i++) {
        struct stUn_imopVarPre27 *_imopVarPre354;
        void *_imopVarPre355;
        _imopVarPre354 = &g->vertices;
        _imopVarPre355 = elem_at(_imopVarPre354, i);
        node *u = _imopVarPre355;
        payload *u_data = u->data;
        if (u_data->fragment_id == from) {
            u_data->fragment_id = to;
        }
    }
}
int verify_and_print_solution(graph *g, queuelist *mst) {
    long long int computed_weight = 0;
    int _imopVarPre463;
    _imopVarPre463 = is_ql_queue_empty(mst, 0);
    while (!_imopVarPre463) {
        void *_imopVarPre464;
        _imopVarPre464 = dequeue(mst, 0);
        edge *e = _imopVarPre464;
        computed_weight += e->w;
        ;
        _imopVarPre463 = is_ql_queue_empty(mst, 0);
    }
    long long int actual_weight = 0;
    int done = 0;
    unsigned long int _imopVarPre466;
    void *_imopVarPre467;
    _imopVarPre466 = g->N * sizeof(int);
    _imopVarPre467 = malloc(_imopVarPre466);
    int *in_mst = _imopVarPre467;
    unsigned long int _imopVarPre469;
    void *_imopVarPre470;
    _imopVarPre469 = g->N * sizeof(int);
    _imopVarPre470 = malloc(_imopVarPre469);
    int *parent = _imopVarPre470;
    unsigned long int _imopVarPre472;
    void *_imopVarPre473;
    _imopVarPre472 = g->N * sizeof(int);
    _imopVarPre473 = malloc(_imopVarPre472);
    int *d = _imopVarPre473;
    int i;
    for (i = 0; i < g->N; i++) {
        in_mst[i] = 0;
        parent[i] = -1;
        d[i] = 0x7fffffff;
    }
    d[0] = 0;
    while (done < g->N) {
        done++;
        int min = 0x7fffffff;
        int min_idx = 0;
        int i;
        for (i = 0; i < g->N; i++) {
            int _imopVarPre475;
            _imopVarPre475 = !in_mst[i];
            if (_imopVarPre475) {
                _imopVarPre475 = min >= d[i];
            }
            if (_imopVarPre475) {
                min = d[i];
                min_idx = i;
            }
        }
        struct stUn_imopVarPre27 *_imopVarPre477;
        void *_imopVarPre478;
        _imopVarPre477 = &g->vertices;
        _imopVarPre478 = elem_at(_imopVarPre477, min_idx);
        node *u = _imopVarPre478;
        in_mst[u->label] = 1;
        for (i = 0; i < u->degree; i++) {
            struct stUn_imopVarPre27 *_imopVarPre485;
            void *_imopVarPre486;
            _imopVarPre485 = &u->neighbors;
            _imopVarPre486 = elem_at(_imopVarPre485, i);
            node *v = *((node **) _imopVarPre486);
            int w = g->adj_mat[u->label][v->label];
            int _imopVarPre488;
            _imopVarPre488 = in_mst[v->label] == 0;
            if (_imopVarPre488) {
                _imopVarPre488 = w < d[v->label];
            }
            if (_imopVarPre488) {
                d[v->label] = w;
                parent[v->label] = u->label;
            }
        }
    }
    for (i = 1; i < g->N; i++) {
        actual_weight += g->adj_mat[i][parent[i]];
    }
    if (actual_weight == computed_weight) {
        ;
    } else {
        ;
    }
    return computed_weight != actual_weight;
}
int main(int argc, char *argv[]) {
    int N;
    int M;
    graph *g;
    int iterate;
    int iterations = 1;
    int _imopVarPre490;
    _imopVarPre490 = input_through_argv(argc, argv);
    if ((iterate = _imopVarPre490)) {
        char *_imopVarPre492;
        struct _IO_FILE *_imopVarPre493;
        _imopVarPre492 = argv[2];
        _imopVarPre493 = fopen(_imopVarPre492, "r");
        FILE *in = _imopVarPre493;
        int *_imopVarPre495;
        _imopVarPre495 = &N;
        fscanf(in, "%d\n", _imopVarPre495);
        g = new_graph(N, 0);
        int _imopVarPre496;
        _imopVarPre496 = read_graph(g, in);
        g->M = M = _imopVarPre496;
        read_weights(g, in);
        fclose(in);
        int *_imopVarPre499;
        char *_imopVarPre500;
        _imopVarPre499 = &iterations;
        _imopVarPre500 = argv[3];
        sscanf(_imopVarPre500, "%d", _imopVarPre499);
    } else {
        N = 16;
        M = 64;
        if (argc > 1) {
            int *_imopVarPre503;
            char *_imopVarPre504;
            _imopVarPre503 = &N;
            _imopVarPre504 = argv[1];
            sscanf(_imopVarPre504, "%d", _imopVarPre503);
            int *_imopVarPre507;
            char *_imopVarPre508;
            _imopVarPre507 = &M;
            _imopVarPre508 = argv[2];
            sscanf(_imopVarPre508, "%d", _imopVarPre507);
        }
        g = generate_new_connected_graph(N, M);
    }
    long long duration = 0;
    int verification;
    int i;
    for (i = 0; i < iterations; i++) {
        int i_imopVarPre5;
        int nodes_yet_to_recv;
        int i_imopVarPre0;
        int i_imopVarPre3;
        int condVar_imopVarPre1;
        int i_imopVarPre6;
        int condVar_imopVarPre2;
        int ok;
        int condVar_imopVarPre7;
        int i_imopVarPre4;
        int _imopVarPre527;
        unsigned long int _imopVarPre511;
        int _imopVarPre512;
        struct stUn_imopVarPre31 *_imopVarPre513;
        _imopVarPre511 = sizeof(message);
        _imopVarPre512 = g->N;
        _imopVarPre513 = new_queuelist(_imopVarPre512, _imopVarPre511);
        queuelist *msgs = _imopVarPre513;
        unsigned long int _imopVarPre516;
        int _imopVarPre517;
        struct stUn_imopVarPre31 *_imopVarPre518;
        _imopVarPre516 = sizeof(message);
        _imopVarPre517 = g->N;
        _imopVarPre518 = new_queuelist(_imopVarPre517, _imopVarPre516);
        queuelist *tmp_msgs = _imopVarPre518;
        unsigned long int _imopVarPre521;
        int _imopVarPre522;
        struct stUn_imopVarPre31 *_imopVarPre523;
        _imopVarPre521 = sizeof(edge);
        _imopVarPre522 = g->N;
        _imopVarPre523 = new_queuelist(_imopVarPre522, _imopVarPre521);
        queuelist *blues = _imopVarPre523;
        unsigned long int _imopVarPre525;
        struct stUn_imopVarPre31 *_imopVarPre526;
        _imopVarPre525 = sizeof(edge);
        _imopVarPre526 = new_queuelist(1, _imopVarPre525);
        /**/
        queuelist *mst = _imopVarPre526;
#pragma omp parallel
        {
            signed long long int _imopVarPre528;
#pragma omp master
            {
                begin_timer();
                ;
            }
#pragma omp for nowait
            for (i_imopVarPre4 = 0; i_imopVarPre4 < g->N; i_imopVarPre4++) {
                struct stUn_imopVarPre27 *_imopVarPre345;
                void *_imopVarPre346;
                _imopVarPre345 = &g->vertices;
                _imopVarPre346 = elem_at(_imopVarPre345, i_imopVarPre4);
                node *u = _imopVarPre346;
                unsigned long int _imopVarPre348;
                void *_imopVarPre349;
                _imopVarPre348 = sizeof(payload);
                _imopVarPre349 = malloc(_imopVarPre348);
                payload *u_data = _imopVarPre349;
                u->data = u_data;
                u_data->fragment_id = u->label;
                u_data->tmp_fragment_id = u->label;
                u_data->received_first_message = 0;
                u_data->b = ((void *) 0);
            }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp master
            {
                _imopVarPre527 = multiple_fragments(g);
            }
#pragma omp master
            {
                condVar_imopVarPre7 = _imopVarPre527;
            }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
            while (condVar_imopVarPre7) {
#pragma omp master
                {
                    nodes_yet_to_recv = 1;
                    ;
                }
#pragma omp for nowait
                for (i_imopVarPre3 = 0; i_imopVarPre3 < g->N; i_imopVarPre3++) {
                    struct stUn_imopVarPre27 *_imopVarPre357;
                    void *_imopVarPre358;
                    _imopVarPre357 = &g->vertices;
                    _imopVarPre358 = elem_at(_imopVarPre357, i_imopVarPre3);
                    node *u = _imopVarPre358;
                    payload *u_data = u->data;
                    u_data->received_first_message = 0;
                    if (u_data->fragment_id != u->label) {
                        continue;
                    }
                    message m = {-1};
                    struct stUn_imopVarPre32 *_imopVarPre361;
                    int _imopVarPre362;
                    _imopVarPre361 = &m;
                    _imopVarPre362 = u->label;
                    enqueue(msgs, _imopVarPre362, _imopVarPre361);
                }
#pragma omp master
                {
                    ;
                }
#pragma omp master
                {
                    condVar_imopVarPre1 = nodes_yet_to_recv;
                }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
                while (condVar_imopVarPre1) {
#pragma omp master
                    {
                        ;
                    }
#pragma omp for nowait
                    for (i_imopVarPre0 = 0; i_imopVarPre0 < g->N; i_imopVarPre0++) {
                        struct stUn_imopVarPre27 *_imopVarPre364;
                        void *_imopVarPre365;
                        _imopVarPre364 = &g->vertices;
                        _imopVarPre365 = elem_at(_imopVarPre364, i_imopVarPre0);
                        node *u = _imopVarPre365;
                        payload *u_data = u->data;
                        if (u_data->received_first_message) {
                            continue;
                        }
                        int _imopVarPre368;
                        int _imopVarPre369;
                        _imopVarPre368 = u->label;
                        _imopVarPre369 = is_ql_queue_empty(msgs, _imopVarPre368);
                        while (!_imopVarPre369) {
                            u_data->received_first_message = 1;
                            int _imopVarPre371;
                            void *_imopVarPre372;
                            _imopVarPre371 = u->label;
                            _imopVarPre372 = dequeue(msgs, _imopVarPre371);
                            message *m = _imopVarPre372;
                            int j;
                            for (j = 0; j < u->degree; j++) {
                                struct stUn_imopVarPre27 *_imopVarPre379;
                                void *_imopVarPre380;
                                _imopVarPre379 = &u->neighbors;
                                _imopVarPre380 = elem_at(_imopVarPre379, j);
                                node *v = *((node **) _imopVarPre380);
                                payload *v_data = v->data;
                                if (v->label == m->from) {
                                    continue;
                                }
                                if (v_data->fragment_id != u_data->fragment_id) {
                                    edge b = {u->label, v->label , g->adj_mat[u->label][v->label]};
                                    struct stUn_imopVarPre33 *_imopVarPre383;
                                    int _imopVarPre384;
                                    _imopVarPre383 = &b;
                                    _imopVarPre384 = u_data->fragment_id;
                                    enqueue(blues, _imopVarPre384, _imopVarPre383);
                                } else {
                                    message mx = {u->label};
                                    struct stUn_imopVarPre32 *_imopVarPre387;
                                    int _imopVarPre388;
                                    _imopVarPre387 = &mx;
                                    _imopVarPre388 = v->label;
                                    enqueue(tmp_msgs, _imopVarPre388, _imopVarPre387);
                                }
                            }
                            _imopVarPre368 = u->label;
                            _imopVarPre369 = is_ql_queue_empty(msgs, _imopVarPre368);
                        }
                    }
#pragma omp master
                    {
                        ;
                    }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp for nowait
                    for (i_imopVarPre0 = 0; i_imopVarPre0 < g->N; i_imopVarPre0++) {
                        struct stUn_imopVarPre27 *_imopVarPre390;
                        void *_imopVarPre391;
                        _imopVarPre390 = &g->vertices;
                        _imopVarPre391 = elem_at(_imopVarPre390, i_imopVarPre0);
                        node *u = _imopVarPre391;
                        payload *u_data = u->data;
                        int _imopVarPre394;
                        int _imopVarPre395;
                        _imopVarPre394 = u->label;
                        _imopVarPre395 = is_ql_queue_empty(tmp_msgs, _imopVarPre394);
                        while (!_imopVarPre395) {
                            int _imopVarPre397;
                            void *_imopVarPre398;
                            _imopVarPre397 = u->label;
                            _imopVarPre398 = dequeue(tmp_msgs, _imopVarPre397);
                            message *m = _imopVarPre398;
                            if (!u_data->received_first_message) {
                                int _imopVarPre400;
                                _imopVarPre400 = u->label;
                                enqueue(msgs, _imopVarPre400, m);
                            }
                            _imopVarPre394 = u->label;
                            _imopVarPre395 = is_ql_queue_empty(tmp_msgs, _imopVarPre394);
                        }
                    }
#pragma omp master
                    {
                        nodes_yet_to_recv = 0;
                        ;
                    }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp for nowait
                    for (i_imopVarPre0 = 0; i_imopVarPre0 < g->N; i_imopVarPre0++) {
                        struct stUn_imopVarPre27 *_imopVarPre402;
                        void *_imopVarPre403;
                        _imopVarPre402 = &g->vertices;
                        _imopVarPre403 = elem_at(_imopVarPre402, i_imopVarPre0);
                        node *u = _imopVarPre403;
                        payload *u_data = u->data;
                        if (!u_data->received_first_message) {
                            nodes_yet_to_recv = 1;
                        }
                    }
#pragma omp master
                    {
                        ;
                    }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp master
                    {
                        condVar_imopVarPre1 = nodes_yet_to_recv;
                    }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
                }
#pragma omp master
                {
                    ;
                }
#pragma omp for nowait
                for (i_imopVarPre3 = 0; i_imopVarPre3 < g->N; i_imopVarPre3++) {
                    struct stUn_imopVarPre27 *_imopVarPre405;
                    void *_imopVarPre406;
                    _imopVarPre405 = &g->vertices;
                    _imopVarPre406 = elem_at(_imopVarPre405, i_imopVarPre3);
                    node *u = _imopVarPre406;
                    payload *u_data = u->data;
                    if (u_data->fragment_id != u->label) {
                        continue;
                    }
                    edge *min_edge = ((void *) 0);
                    int _imopVarPre409;
                    int _imopVarPre410;
                    _imopVarPre409 = u->label;
                    _imopVarPre410 = is_ql_queue_empty(blues, _imopVarPre409);
                    while (!_imopVarPre410) {
                        int _imopVarPre412;
                        void *_imopVarPre413;
                        _imopVarPre412 = u->label;
                        _imopVarPre413 = dequeue(blues, _imopVarPre412);
                        edge *b = _imopVarPre413;
                        if (min_edge == ((void *) 0)) {
                            min_edge = b;
                            continue;
                        }
                        int b_score = b->u * g->N + b->v;
                        if (b->u > b->v) {
                            b_score = b->v * g->N + b->u;
                        }
                        int min_score = min_edge->u * g->N + min_edge->v;
                        if (min_edge->u > min_edge->v) {
                            min_score = min_edge->v * g->N + min_edge->u;
                        }
                        int _imopVarPre414;
                        int _imopVarPre418;
                        _imopVarPre414 = (b->w < min_edge->w);
                        if (!_imopVarPre414) {
                            _imopVarPre418 = b->w == min_edge->w;
                            if (_imopVarPre418) {
                                _imopVarPre418 = b_score < min_score;
                            }
                            _imopVarPre414 = _imopVarPre418;
                        }
                        if (_imopVarPre414) {
                            min_edge = b;
                        }
                        _imopVarPre409 = u->label;
                        _imopVarPre410 = is_ql_queue_empty(blues, _imopVarPre409);
                    }
                    int _imopVarPre421;
                    struct stUn_imopVarPre27 *_imopVarPre422;
                    void *_imopVarPre423;
                    _imopVarPre421 = min_edge->u;
                    _imopVarPre422 = &g->vertices;
                    _imopVarPre423 = elem_at(_imopVarPre422, _imopVarPre421);
                    node *future_leader = _imopVarPre423;
                    payload *future_leader_data = future_leader->data;
                    u_data->b = min_edge;
                    future_leader_data->b = min_edge;
                }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp master
                {
                    ;
                }
#pragma omp for nowait
                for (i_imopVarPre6 = 0; i_imopVarPre6 < g->N; i_imopVarPre6++) {
                    struct stUn_imopVarPre27 *_imopVarPre425;
                    void *_imopVarPre426;
                    _imopVarPre425 = &g->vertices;
                    _imopVarPre426 = elem_at(_imopVarPre425, i_imopVarPre6);
                    node *u = _imopVarPre426;
                    payload *u_data = u->data;
                    int _imopVarPre429;
                    struct stUn_imopVarPre27 *_imopVarPre430;
                    void *_imopVarPre431;
                    _imopVarPre429 = u_data->fragment_id;
                    _imopVarPre430 = &g->vertices;
                    _imopVarPre431 = elem_at(_imopVarPre430, _imopVarPre429);
                    node *leader = _imopVarPre431;
                    payload *leader_data = leader->data;
                    u_data->tmp_fragment_id = leader_data->b->u;
                }
#pragma omp master
                {
                    ;
                }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp for nowait
                for (i_imopVarPre6 = 0; i_imopVarPre6 < g->N; i_imopVarPre6++) {
                    struct stUn_imopVarPre27 *_imopVarPre433;
                    void *_imopVarPre434;
                    _imopVarPre433 = &g->vertices;
                    _imopVarPre434 = elem_at(_imopVarPre433, i_imopVarPre6);
                    node *u = _imopVarPre434;
                    payload *u_data = u->data;
                    u_data->fragment_id = u_data->tmp_fragment_id;
                }
#pragma omp master
                {
                    ok = 0;
                    condVar_imopVarPre2 = (ok < 2);
                }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
                for (; condVar_imopVarPre2; ) {
#pragma omp master
                    {
                        ;
                    }
#pragma omp for nowait
                    for (i_imopVarPre5 = 0; i_imopVarPre5 < g->N; i_imopVarPre5++) {
                        struct stUn_imopVarPre27 *_imopVarPre436;
                        void *_imopVarPre437;
                        _imopVarPre436 = &g->vertices;
                        _imopVarPre437 = elem_at(_imopVarPre436, i_imopVarPre5);
                        node *u = _imopVarPre437;
                        payload *u_data = u->data;
                        if (u_data->fragment_id != u->label) {
                            continue;
                        }
// #pragma omp dummyFlush CRITICAL_START
#pragma omp critical
                        {
                            int _imopVarPre440;
                            struct stUn_imopVarPre27 *_imopVarPre441;
                            void *_imopVarPre442;
                            _imopVarPre440 = u_data->b->v;
                            _imopVarPre441 = &g->vertices;
                            _imopVarPre442 = elem_at(_imopVarPre441, _imopVarPre440);
                            node *v = _imopVarPre442;
                            payload *v_data = v->data;
                            int _imopVarPre445;
                            struct stUn_imopVarPre27 *_imopVarPre446;
                            void *_imopVarPre447;
                            _imopVarPre445 = v_data->fragment_id;
                            _imopVarPre446 = &g->vertices;
                            _imopVarPre447 = elem_at(_imopVarPre446, _imopVarPre445);
                            node *v_leader = _imopVarPre447;
                            payload *v_leader_data = v_leader->data;
                            int _imopVarPre454;
                            int _imopVarPre455;
                            _imopVarPre454 = u->label == v_leader_data->b->v;
                            if (_imopVarPre454) {
                                _imopVarPre455 = v_leader_data->b->u == v->label;
                                if (_imopVarPre455) {
                                    _imopVarPre455 = u_data->b->v == v->label;
                                }
                                _imopVarPre454 = _imopVarPre455;
                            }
                            int conflicting_merges = _imopVarPre454;
                            if (conflicting_merges == ok) {
                                int _imopVarPre458;
                                int _imopVarPre459;
                                _imopVarPre458 = v_leader->label;
                                _imopVarPre459 = u->label;
                                change_fragment(g, _imopVarPre459, _imopVarPre458);
                                edge m = {u->label, v->label , g->adj_mat[u->label][v->label]};
                                struct stUn_imopVarPre33 *_imopVarPre461;
                                _imopVarPre461 = &m;
                                enqueue(mst, 0, _imopVarPre461);
                            }
                        }
// #pragma omp dummyFlush CRITICAL_END
                    }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp master
                    {
                        ok++;
                        condVar_imopVarPre2 = (ok < 2);
                    }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
                }
#pragma omp master
                {
                    _imopVarPre527 = multiple_fragments(g);
                }
#pragma omp master
                {
                    condVar_imopVarPre7 = _imopVarPre527;
                }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
            }
#pragma omp master
            {
                _imopVarPre528 = time_elapsed();
                duration += _imopVarPre528;
                free_queuelist(msgs);
                free_queuelist(tmp_msgs);
                free_queuelist(blues);
            }
        }
        verification = verify_and_print_solution(g, mst);
        free_queuelist(mst);
    }
    if (iterate) {
        double _imopVarPre530;
        _imopVarPre530 = ((double) duration) / iterations;
        printf("%.2lf\n", _imopVarPre530);
    }
    return verification;
}
