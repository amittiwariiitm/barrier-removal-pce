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
    int _imopVarPre114;
    char *_imopVarPre115;
    int _imopVarPre116;
    _imopVarPre114 = argc == 4;
    if (_imopVarPre114) {
        _imopVarPre115 = argv[1];
        _imopVarPre116 = strcmp(_imopVarPre115, "-");
        _imopVarPre114 = _imopVarPre116 == 0;
    }
    if (_imopVarPre114) {
        return 2;
    }
    return 0;
}
void begin_timer() {
    struct timespec *_imopVarPre118;
    _imopVarPre118 = &start_time;
    clock_gettime(2, _imopVarPre118);
}
long long time_elapsed() {
    struct timespec end_time;
    struct timespec *_imopVarPre120;
    _imopVarPre120 = &end_time;
    clock_gettime(2, _imopVarPre120);
    long long int s = end_time.tv_sec - start_time.tv_sec;
    long long int ns = end_time.tv_nsec - start_time.tv_nsec;
    return (s * ((long long) 1e9)) + ns;
}
void initialize_vector(vector *v, size_t object_size) {
    v->object_size = object_size;
    v->allocated = 16;
    unsigned long int _imopVarPre122;
    void *_imopVarPre123;
    _imopVarPre122 = v->allocated * v->object_size;
    _imopVarPre123 = malloc(_imopVarPre122);
    v->items = _imopVarPre123;
    v->used = 0;
}
vector *new_vector(size_t object_size) {
    unsigned long int _imopVarPre126;
    void *_imopVarPre127;
    _imopVarPre126 = 1 * sizeof(vector);
    _imopVarPre127 = malloc(_imopVarPre126);
    vector *v = (vector *) _imopVarPre127;
    initialize_vector(v, object_size);
    return v;
}
void append_to_vector(vector *v, void *object) {
    while (v->allocated <= v->used + 1) {
        v->allocated *= 2;
        unsigned long int _imopVarPre130;
        void *_imopVarPre131;
        void *_imopVarPre132;
        _imopVarPre130 = v->allocated * v->object_size;
        _imopVarPre131 = v->items;
        _imopVarPre132 = realloc(_imopVarPre131, _imopVarPre130);
        v->items = _imopVarPre132;
    }
    unsigned long int _imopVarPre135;
    void *_imopVarPre136;
    _imopVarPre135 = v->object_size;
    _imopVarPre136 = v->items + (v->used * v->object_size);
    memcpy(_imopVarPre136, object, _imopVarPre135);
    v->used++;
}
void *elem_at(vector *v, size_t idx) {
    return v->items + (idx * v->object_size);
}
void free_vector(vector *v) {
    void *_imopVarPre138;
    _imopVarPre138 = v->items;
    free(_imopVarPre138);
    free(v);
}
graph *new_graph(int N, int M) {
    unsigned long int _imopVarPre140;
    void *_imopVarPre141;
    _imopVarPre140 = 1 * sizeof(graph);
    _imopVarPre141 = malloc(_imopVarPre140);
    graph *g = _imopVarPre141;
    g->N = N;
    g->M = M;
    unsigned long int _imopVarPre144;
    struct stUn_imopVarPre27 *_imopVarPre145;
    _imopVarPre144 = sizeof(node);
    _imopVarPre145 = &g->vertices;
    initialize_vector(_imopVarPre145, _imopVarPre144);
    int i;
    for (i = 0; i < g->N; i++) {
        node u;
        unsigned long int _imopVarPre148;
        struct stUn_imopVarPre27 *_imopVarPre149;
        _imopVarPre148 = sizeof(node *);
        _imopVarPre149 = &u.neighbors;
        initialize_vector(_imopVarPre149, _imopVarPre148);
        u.degree = 0;
        u.data = ((void *) 0);
        u.label = i;
        u.weight = 0;
        struct stUn_imopVarPre28 *_imopVarPre152;
        struct stUn_imopVarPre27 *_imopVarPre153;
        _imopVarPre152 = &u;
        _imopVarPre153 = &g->vertices;
        append_to_vector(_imopVarPre153, _imopVarPre152);
    }
    unsigned long int _imopVarPre156;
    void *_imopVarPre157;
    _imopVarPre156 = N * sizeof(int *);
    _imopVarPre157 = malloc(_imopVarPre156);
    g->adj_mat = (int **) _imopVarPre157;
    for (i = 0; i < g->N; i++) {
        unsigned long int _imopVarPre160;
        void *_imopVarPre161;
        _imopVarPre160 = sizeof(int);
        _imopVarPre161 = calloc(N, _imopVarPre160);
        g->adj_mat[i] = (int *) _imopVarPre161;
    }
    g->root = ((void *) 0);
    ;
    return g;
}
int read_graph(graph *g, FILE *in) {
    int M = 0;
    int _imopVarPre163;
    void *_imopVarPre164;
    _imopVarPre163 = g->N + 2;
    _imopVarPre164 = malloc(_imopVarPre163);
    char *line = _imopVarPre164;
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
    struct stUn_imopVarPre27 *_imopVarPre170;
    void *_imopVarPre171;
    _imopVarPre170 = &g->vertices;
    _imopVarPre171 = elem_at(_imopVarPre170, i);
    node *node_u = _imopVarPre171;
    struct stUn_imopVarPre27 *_imopVarPre173;
    void *_imopVarPre174;
    _imopVarPre173 = &g->vertices;
    _imopVarPre174 = elem_at(_imopVarPre173, j);
    node *node_v = _imopVarPre174;
    struct stUn_imopVarPre28 **_imopVarPre177;
    struct stUn_imopVarPre27 *_imopVarPre178;
    _imopVarPre177 = &node_v;
    _imopVarPre178 = &node_u->neighbors;
    append_to_vector(_imopVarPre178, _imopVarPre177);
    struct stUn_imopVarPre28 **_imopVarPre181;
    struct stUn_imopVarPre27 *_imopVarPre182;
    _imopVarPre181 = &node_u;
    _imopVarPre182 = &node_v->neighbors;
    append_to_vector(_imopVarPre182, _imopVarPre181);
    node_u->degree++;
    node_v->degree++;
    g->adj_mat[i][j] = g->adj_mat[j][i] = 1;
}
void free_graph(graph *g) {
    ;
    if (g->adj_mat != ((void *) 0)) {
        int i;
        for (i = 0; i < g->N; i++) {
            int *_imopVarPre184;
            _imopVarPre184 = g->adj_mat[i];
            free(_imopVarPre184);
        }
        int **_imopVarPre186;
        _imopVarPre186 = g->adj_mat;
        free(_imopVarPre186);
    }
}
int is_connected(graph *g) {
    unsigned long int _imopVarPre189;
    int _imopVarPre190;
    void *_imopVarPre191;
    _imopVarPre189 = sizeof(int);
    _imopVarPre190 = g->N;
    _imopVarPre191 = calloc(_imopVarPre190, _imopVarPre189);
    int *visited = _imopVarPre191;
    struct stUn_imopVarPre27 *_imopVarPre193;
    void *_imopVarPre194;
    _imopVarPre193 = &g->vertices;
    _imopVarPre194 = elem_at(_imopVarPre193, 0);
    node *root = _imopVarPre194;
    visited[root->label] = 1;
    unsigned long int _imopVarPre196;
    struct stUn_imopVarPre27 *_imopVarPre197;
    _imopVarPre196 = sizeof(node *);
    _imopVarPre197 = new_vector(_imopVarPre196);
    vector *queue = _imopVarPre197;
    struct stUn_imopVarPre28 **_imopVarPre199;
    _imopVarPre199 = &root;
    append_to_vector(queue, _imopVarPre199);
    int queue_position = 0;
    ;
    while (queue_position < queue->used) {
        void *_imopVarPre203;
        _imopVarPre203 = elem_at(queue, queue_position);
        node *cur = *((node **) _imopVarPre203);
        queue_position++;
        int i;
        for (i = 0; i < cur->degree; i++) {
            struct stUn_imopVarPre27 *_imopVarPre210;
            void *_imopVarPre211;
            _imopVarPre210 = &cur->neighbors;
            _imopVarPre211 = elem_at(_imopVarPre210, i);
            node *neighbor = *((node **) _imopVarPre211);
            if (visited[neighbor->label] > 0) {
                continue;
            }
            visited[neighbor->label] = 1;
            struct stUn_imopVarPre28 **_imopVarPre213;
            _imopVarPre213 = &neighbor;
            append_to_vector(queue, _imopVarPre213);
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
    struct stUn_imopVarPre29 *_imopVarPre214;
    _imopVarPre214 = new_graph(N, M);
    graph *g = _imopVarPre214;
    ;
    int edges_created = 0;
    while (edges_created < M) {
        int _imopVarPre216;
        _imopVarPre216 = rand();
        int u = _imopVarPre216 % N;
        int _imopVarPre218;
        _imopVarPre218 = rand();
        int v = _imopVarPre218 % N;
        if (u == v) {
            continue;
        }
        if (u > v) {
            int *_imopVarPre221;
            int *_imopVarPre222;
            _imopVarPre221 = &v;
            _imopVarPre222 = &u;
            swap(_imopVarPre222, _imopVarPre221);
        }
        if (g->adj_mat[u][v] != 0) {
            continue;
        }
        int _imopVarPre224;
        _imopVarPre224 = rand();
        g->adj_mat[u][v] = g->adj_mat[v][u] = 1 + _imopVarPre224 % 100;
        add_edge(g, u, v);
        edges_created += 1;
    }
    ;
    return g;
}
graph *generate_new_connected_graph(int N, int M) {
    graph *g = ((void *) 0);
    int attempts = 0;
    int _imopVarPre226;
    do {
        attempts++;
        if (g != ((void *) 0)) {
            free_graph(g);
        }
        ;
        g = generate_new_graph(N, M);
        _imopVarPre226 = is_connected(g);
    } while (!_imopVarPre226);
    ;
    return g;
}
omp_lock_t *new_locks(int );
omp_lock_t *new_locks(int N) {
    ;
    unsigned long int _imopVarPre303;
    void *_imopVarPre304;
    _imopVarPre303 = N * sizeof(omp_lock_t);
    _imopVarPre304 = malloc(_imopVarPre303);
    omp_lock_t *locks = _imopVarPre304;
    ;
    int i;
    for (i = 0; i < N; i++) {
        struct stUn_imopVarPre4 *_imopVarPre306;
        _imopVarPre306 = locks + i;
        omp_init_lock(_imopVarPre306);
// #pragma omp dummyFlush LOCK_WRITE_END
    }
    return locks;
}
struct stUn_imopVarPre31 {
    int N;
    vector **queues;
    int *front;
    omp_lock_t *locks;
} ;
typedef struct stUn_imopVarPre31 queuelist;
queuelist *new_queuelist(int , size_t );
void enqueue(queuelist *, int  , void *);
void *dequeue(queuelist *, int );
int is_ql_queue_empty(queuelist *, int );
queuelist *new_queuelist(int N, size_t elem_size) {
    unsigned long int _imopVarPre310;
    void *_imopVarPre311;
    _imopVarPre310 = sizeof(queuelist);
    _imopVarPre311 = malloc(_imopVarPre310);
    queuelist *ql = _imopVarPre311;
    ql->N = N;
    ;
    unsigned long int _imopVarPre313;
    void *_imopVarPre314;
    _imopVarPre313 = N * sizeof(vector *);
    _imopVarPre314 = malloc(_imopVarPre313);
    vector **queues = _imopVarPre314;
    ;
    int i;
    for (i = 0; i < N; i++) {
        struct stUn_imopVarPre27 *_imopVarPre315;
        _imopVarPre315 = new_vector(elem_size);
        queues[i] = _imopVarPre315;
    }
    ql->queues = queues;
    unsigned long int _imopVarPre317;
    void *_imopVarPre318;
    _imopVarPre317 = N * sizeof(int);
    _imopVarPre318 = malloc(_imopVarPre317);
    ql->front = _imopVarPre318;
    for (i = 0; i < N; i++) {
        ql->front[i] = 0;
    }
    struct stUn_imopVarPre4 *_imopVarPre319;
    _imopVarPre319 = new_locks(N);
    ql->locks = _imopVarPre319;
    return ql;
}
void enqueue(queuelist *ql, int i , void *object) {
    struct stUn_imopVarPre4 *_imopVarPre327;
    _imopVarPre327 = ql->locks + i;
// #pragma omp dummyFlush LOCK_MODIFY_START
    omp_set_lock(_imopVarPre327);
// #pragma omp dummyFlush LOCK_MODIFY_END
    struct stUn_imopVarPre27 *_imopVarPre329;
    _imopVarPre329 = ql->queues[i];
    append_to_vector(_imopVarPre329, object);
    struct stUn_imopVarPre4 *_imopVarPre331;
    _imopVarPre331 = ql->locks + i;
    omp_unset_lock(_imopVarPre331);
// #pragma omp dummyFlush LOCK_WRITE_END
}
void *dequeue(queuelist *ql, int i) {
    struct stUn_imopVarPre4 *_imopVarPre333;
    _imopVarPre333 = ql->locks + i;
// #pragma omp dummyFlush LOCK_MODIFY_START
    omp_set_lock(_imopVarPre333);
// #pragma omp dummyFlush LOCK_MODIFY_END
    void *object;
    vector *v = ql->queues[i];
    if (ql->front[i] == v->used) {
        object = ((void *) 0);
        goto end;
    }
    int _imopVarPre335;
    void *_imopVarPre336;
    _imopVarPre335 = ql->front[i];
    _imopVarPre336 = elem_at(v, _imopVarPre335);
    object = _imopVarPre336;
    ql->front[i]++;
    if (ql->front[i] == v->used) {
        ql->front[i] = 0;
        v->used = 0;
    }
    struct stUn_imopVarPre4 *_imopVarPre338;
    end: _imopVarPre338 = ql->locks + i;
    omp_unset_lock(_imopVarPre338);
// #pragma omp dummyFlush LOCK_WRITE_END
    return object;
}
int is_ql_queue_empty(queuelist *ql, int i) {
    struct stUn_imopVarPre4 *_imopVarPre340;
    _imopVarPre340 = ql->locks + i;
// #pragma omp dummyFlush LOCK_MODIFY_START
    omp_set_lock(_imopVarPre340);
// #pragma omp dummyFlush LOCK_MODIFY_END
    vector *v = ql->queues[i];
    int result = ql->front[i] == v->used;
    struct stUn_imopVarPre4 *_imopVarPre342;
    _imopVarPre342 = ql->locks + i;
    omp_unset_lock(_imopVarPre342);
// #pragma omp dummyFlush LOCK_WRITE_END
    return result;
}
struct stUn_imopVarPre32 {
    int distance;
} ;
typedef struct stUn_imopVarPre32 payload;
struct stUn_imopVarPre33 {
    int from;
    int y;
} ;
typedef struct stUn_imopVarPre33 message;
int ROOT;
void initialize_graph(graph *g) {
    int i;
    for (i = 0; i < g->N; i++) {
        struct stUn_imopVarPre27 *_imopVarPre344;
        void *_imopVarPre345;
        _imopVarPre344 = &g->vertices;
        _imopVarPre345 = elem_at(_imopVarPre344, i);
        node *u = _imopVarPre345;
        unsigned long int _imopVarPre347;
        void *_imopVarPre348;
        _imopVarPre347 = sizeof(payload);
        _imopVarPre348 = malloc(_imopVarPre347);
        payload *data = _imopVarPre348;
        data->distance = 0x7fffffff;
        u->data = data;
    }
    struct stUn_imopVarPre27 *_imopVarPre350;
    void *_imopVarPre351;
    _imopVarPre350 = &g->vertices;
    _imopVarPre351 = elem_at(_imopVarPre350, ROOT);
    node *root = _imopVarPre351;
    payload *root_data = root->data;
    root_data->distance = 0;
}
void root_message(graph *g, queuelist *recv) {
    ;
    struct stUn_imopVarPre27 *_imopVarPre353;
    void *_imopVarPre354;
    _imopVarPre353 = &g->vertices;
    _imopVarPre354 = elem_at(_imopVarPre353, ROOT);
    node *root = _imopVarPre354;
    int j;
    for (j = 0; j < root->degree; j++) {
        struct stUn_imopVarPre27 *_imopVarPre361;
        void *_imopVarPre362;
        _imopVarPre361 = &root->neighbors;
        _imopVarPre362 = elem_at(_imopVarPre361, j);
        node *u = *((node **) _imopVarPre362);
        message m = {ROOT, 1};
        struct stUn_imopVarPre33 *_imopVarPre365;
        int _imopVarPre366;
        _imopVarPre365 = &m;
        _imopVarPre366 = u->label;
        enqueue(recv, _imopVarPre366, _imopVarPre365);
    }
}
int main(int argc, char *argv[]) {
    int N;
    int M;
    graph *g;
    int iterate;
    int iterations = 1;
    int _imopVarPre408;
    _imopVarPre408 = input_through_argv(argc, argv);
    if ((iterate = _imopVarPre408)) {
        char *_imopVarPre410;
        struct _IO_FILE *_imopVarPre411;
        _imopVarPre410 = argv[2];
        _imopVarPre411 = fopen(_imopVarPre410, "r");
        FILE *in = _imopVarPre411;
        int *_imopVarPre413;
        _imopVarPre413 = &N;
        fscanf(in, "%d\n", _imopVarPre413);
        g = new_graph(N, 0);
        int *_imopVarPre415;
        _imopVarPre415 = &ROOT;
        fscanf(in, "%d\n", _imopVarPre415);
        int _imopVarPre416;
        _imopVarPre416 = read_graph(g, in);
        g->M = M = _imopVarPre416;
        fclose(in);
        int *_imopVarPre419;
        char *_imopVarPre420;
        _imopVarPre419 = &iterations;
        _imopVarPre420 = argv[3];
        sscanf(_imopVarPre420, "%d", _imopVarPre419);
    } else {
        N = 16;
        M = 64;
        if (argc > 1) {
            int *_imopVarPre423;
            char *_imopVarPre424;
            _imopVarPre423 = &N;
            _imopVarPre424 = argv[1];
            sscanf(_imopVarPre424, "%d", _imopVarPre423);
            int *_imopVarPre427;
            char *_imopVarPre428;
            _imopVarPre427 = &M;
            _imopVarPre428 = argv[2];
            sscanf(_imopVarPre428, "%d", _imopVarPre427);
        }
        g = generate_new_connected_graph(N, M);
        ROOT = 0;
    }
    long long duration = 0;
    int i;
    for (i = 0; i < iterations; i++) {
        int condVar_imopVarPre7;
        int i_imopVarPre1;
        int i_imopVarPre2;
        int i_imopVarPre5;
        int N_imopVarPre0;
        int result_imopVarPre6;
        int _imopVarPre436;
        int _imopVarPre437;
        int N_imopVarPre3;
        int result = 0;
        int i_imopVarPre4;
        unsigned long int _imopVarPre430;
        struct stUn_imopVarPre31 *_imopVarPre431;
        _imopVarPre430 = sizeof(message);
        _imopVarPre431 = new_queuelist(N, _imopVarPre430);
        queuelist *recv = _imopVarPre431;
        unsigned long int _imopVarPre433;
        struct stUn_imopVarPre31 *_imopVarPre434;
        _imopVarPre433 = sizeof(message);
        _imopVarPre434 = new_queuelist(N, _imopVarPre433);
        queuelist *send = _imopVarPre434;
        begin_timer();
        initialize_graph(g);
        root_message(g, recv);
#pragma omp parallel
        {
            signed long long int _imopVarPre438;
#pragma omp master
            {
                _imopVarPre436 = g->N;
                N_imopVarPre3 = _imopVarPre436;
            }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp for nowait
            for (i_imopVarPre4 = 0; i_imopVarPre4 < N_imopVarPre3; i_imopVarPre4++) {
                int _imopVarPre368;
                _imopVarPre368 = is_ql_queue_empty(recv, i_imopVarPre4);
                if (!_imopVarPre368) {
                    result = 1;
                }
            }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp master
            {
                ;
                _imopVarPre437 = result;
            }
#pragma omp master
            {
                condVar_imopVarPre7 = _imopVarPre437;
            }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
            while (condVar_imopVarPre7) {
#pragma omp master
                {
                    ;
                }
#pragma omp for nowait
                for (i_imopVarPre2 = 0; i_imopVarPre2 < g->N; i_imopVarPre2++) {
                    struct stUn_imopVarPre27 *_imopVarPre370;
                    void *_imopVarPre371;
                    _imopVarPre370 = &g->vertices;
                    _imopVarPre371 = elem_at(_imopVarPre370, i_imopVarPre2);
                    node *u = _imopVarPre371;
                    payload *u_data = u->data;
                    int lowest_y = 0x7fffffff;
                    int lowest_from = 0;
                    int _imopVarPre373;
                    _imopVarPre373 = is_ql_queue_empty(recv, i_imopVarPre2);
                    while (!_imopVarPre373) {
                        void *_imopVarPre374;
                        _imopVarPre374 = dequeue(recv, i_imopVarPre2);
                        message *m = _imopVarPre374;
                        if (lowest_y > m->y) {
                            lowest_y = m->y;
                            lowest_from = m->from;
                        }
                        _imopVarPre373 = is_ql_queue_empty(recv, i_imopVarPre2);
                    }
                    int _imopVarPre376;
                    _imopVarPre376 = lowest_y != 0x7fffffff;
                    if (_imopVarPre376) {
                        _imopVarPre376 = lowest_y < u_data->distance;
                    }
                    if (_imopVarPre376) {
                        u_data->distance = lowest_y;
                        int j;
                        for (j = 0; j < u->degree; j++) {
                            struct stUn_imopVarPre27 *_imopVarPre383;
                            void *_imopVarPre384;
                            _imopVarPre383 = &u->neighbors;
                            _imopVarPre384 = elem_at(_imopVarPre383, j);
                            node *v = *((node **) _imopVarPre384);
                            u->data;
                            if (v->label == lowest_from) {
                                continue;
                            }
                            message m = {u->label, lowest_y + 1};
                            struct stUn_imopVarPre33 *_imopVarPre387;
                            int _imopVarPre388;
                            _imopVarPre387 = &m;
                            _imopVarPre388 = v->label;
                            enqueue(send, _imopVarPre388, _imopVarPre387);
                        }
                    }
                }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp master
                {
                    ;
                }
#pragma omp for nowait
                for (i_imopVarPre5 = 0; i_imopVarPre5 < g->N; i_imopVarPre5++) {
                    struct stUn_imopVarPre27 *_imopVarPre390;
                    void *_imopVarPre391;
                    _imopVarPre390 = &g->vertices;
                    _imopVarPre391 = elem_at(_imopVarPre390, i_imopVarPre5);
                    node *u = _imopVarPre391;
                    int _imopVarPre394;
                    int _imopVarPre395;
                    _imopVarPre394 = u->label;
                    _imopVarPre395 = is_ql_queue_empty(send, _imopVarPre394);
                    while (!_imopVarPre395) {
                        int _imopVarPre401;
                        void *_imopVarPre402;
                        int _imopVarPre403;
                        _imopVarPre401 = u->label;
                        _imopVarPre402 = dequeue(send, _imopVarPre401);
                        _imopVarPre403 = u->label;
                        enqueue(recv, _imopVarPre403, _imopVarPre402);
                        _imopVarPre394 = u->label;
                        _imopVarPre395 = is_ql_queue_empty(send, _imopVarPre394);
                    }
                }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp master
                {
                    _imopVarPre436 = g->N;
                    N_imopVarPre0 = _imopVarPre436;
                }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp for nowait
                for (i_imopVarPre1 = 0; i_imopVarPre1 < N_imopVarPre0; i_imopVarPre1++) {
                    int _imopVarPre368;
                    _imopVarPre368 = is_ql_queue_empty(recv, i_imopVarPre1);
                    if (!_imopVarPre368) {
                        result_imopVarPre6 = 1;
                    }
                }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp master
                {
                    ;
                    _imopVarPre437 = result_imopVarPre6;
                }
#pragma omp master
                {
                    condVar_imopVarPre7 = _imopVarPre437;
                }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
            }
#pragma omp master
            {
                _imopVarPre438 = time_elapsed();
                duration += _imopVarPre438;
            }
        }
    }
    if (iterate) {
        double _imopVarPre440;
        _imopVarPre440 = ((double) duration) / iterations;
        printf("%.2lf\n", _imopVarPre440);
    }
    return 0;
}
