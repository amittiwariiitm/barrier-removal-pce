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
void add_edge(graph *, int  , int );
void free_graph(graph *);
int is_connected(graph *);
void swap(int *, int *);
int input_through_argv(int , char **);
struct timespec start_time;
void begin_timer();
long long time_elapsed();
extern double omp_get_wtime(void );
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
void free_locks(omp_lock_t *, int );
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
void free_locks(omp_lock_t *locks, int N) {
    ;
    int i;
    for (i = 0; i < N; i++) {
        struct stUn_imopVarPre4 *_imopVarPre308;
        _imopVarPre308 = locks + i;
        omp_destroy_lock(_imopVarPre308);
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
void free_queuelist(queuelist *ql) {
    int N = ql->N;
    ;
    int i;
    for (i = 0; i < N; i++) {
        struct stUn_imopVarPre27 *_imopVarPre321;
        _imopVarPre321 = ql->queues[i];
        free_vector(_imopVarPre321);
    }
    ;
    int *_imopVarPre323;
    _imopVarPre323 = ql->front;
    free(_imopVarPre323);
    ;
    struct stUn_imopVarPre4 *_imopVarPre325;
    _imopVarPre325 = ql->locks;
    free_locks(_imopVarPre325, N);
    ;
    free(ql);
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
    int x;
    int y;
} ;
typedef struct stUn_imopVarPre32 invitation;
struct stUn_imopVarPre33 {
    int default_leader;
    int leader;
    int committee;
    int min_active;
    invitation invite;
} ;
typedef struct stUn_imopVarPre33 payload;
int min(int a, int b) {
    int _imopVarPre343;
    int _imopVarPre344;
    _imopVarPre343 = a < b;
    if (_imopVarPre343) {
        _imopVarPre344 = a;
    } else {
        _imopVarPre344 = b;
    }
    return _imopVarPre344;
}
void min_invitation(invitation *a, invitation *b) {
    int _imopVarPre345;
    int _imopVarPre349;
    _imopVarPre345 = (b->x < a->x);
    if (!_imopVarPre345) {
        _imopVarPre349 = b->x == a->x;
        if (_imopVarPre349) {
            _imopVarPre349 = b->y < a->y;
        }
        _imopVarPre345 = _imopVarPre349;
    }
    if (_imopVarPre345) {
        a->x = b->x;
        a->y = b->y;
        return;
    }
}
void initialize_graph(graph *g, int *kvals) {
    invitation default_invite = {g->N, g->N};
    int i;
    for (i = 0; i < g->N; i++) {
        struct stUn_imopVarPre27 *_imopVarPre351;
        void *_imopVarPre352;
        _imopVarPre351 = &g->vertices;
        _imopVarPre352 = elem_at(_imopVarPre351, i);
        node *cur = _imopVarPre352;
        unsigned long int _imopVarPre354;
        void *_imopVarPre355;
        _imopVarPre354 = sizeof(payload);
        _imopVarPre355 = malloc(_imopVarPre354);
        payload *data = _imopVarPre355;
        data->leader = kvals[i];
        data->default_leader = kvals[i];
        data->committee = g->N + 1;
        data->min_active = g->N + 1;
        data->invite = default_invite;
        cur->data = data;
    }
}
int verify_and_print_solution(graph *g, int K) {
    int correct = 1;
    unsigned long int _imopVarPre419;
    void *_imopVarPre420;
    _imopVarPre419 = g->N * sizeof(int);
    _imopVarPre420 = malloc(_imopVarPre419);
    int *committee_count = _imopVarPre420;
    int i;
    for (i = 0; i < g->N; i++) {
        committee_count[i] = 0;
    }
    for (i = 0; i < g->N; i++) {
        struct stUn_imopVarPre27 *_imopVarPre422;
        void *_imopVarPre423;
        _imopVarPre422 = &g->vertices;
        _imopVarPre423 = elem_at(_imopVarPre422, i);
        node *cur = _imopVarPre423;
        payload *data = cur->data;
        if (data->committee >= g->N) {
            correct = 0;
            ;
            goto end;
        }
        committee_count[data->committee]++;
        ;
    }
    for (i = 0; i < g->N; i++) {
        if (committee_count[i] > K) {
            ;
            correct = 0;
        }
    }
    end: free(committee_count);
    if (correct) {
        ;
    } else {
        ;
    }
    return !correct;
}
int main(int argc, char *argv[]) {
    int N;
    int M;
    int K;
    int *kvals;
    graph *g;
    int iterate;
    int iterations = 1;
    int _imopVarPre425;
    _imopVarPre425 = input_through_argv(argc, argv);
    if ((iterate = _imopVarPre425)) {
        char *_imopVarPre427;
        struct _IO_FILE *_imopVarPre428;
        _imopVarPre427 = argv[2];
        _imopVarPre428 = fopen(_imopVarPre427, "r");
        FILE *in = _imopVarPre428;
        int *_imopVarPre430;
        _imopVarPre430 = &N;
        fscanf(in, "%d\n", _imopVarPre430);
        unsigned long int _imopVarPre432;
        void *_imopVarPre433;
        _imopVarPre432 = N * sizeof(int);
        _imopVarPre433 = malloc(_imopVarPre432);
        kvals = _imopVarPre433;
        int *_imopVarPre435;
        _imopVarPre435 = &K;
        fscanf(in, "%d\n", _imopVarPre435);
        g = new_graph(N, 0);
        int _imopVarPre436;
        _imopVarPre436 = read_graph(g, in);
        g->M = M = _imopVarPre436;
        fscanf(in, "\n");
        int i;
        for (i = 0; i < N; i++) {
            int *_imopVarPre438;
            _imopVarPre438 = &kvals[i];
            fscanf(in, "%d", _imopVarPre438);
        }
        fclose(in);
        int *_imopVarPre441;
        char *_imopVarPre442;
        _imopVarPre441 = &iterations;
        _imopVarPre442 = argv[3];
        sscanf(_imopVarPre442, "%d", _imopVarPre441);
    } else {
        N = 16;
        M = 64;
        K = 4;
        if (argc > 1) {
            int *_imopVarPre445;
            char *_imopVarPre446;
            _imopVarPre445 = &N;
            _imopVarPre446 = argv[1];
            sscanf(_imopVarPre446, "%d", _imopVarPre445);
            int *_imopVarPre449;
            char *_imopVarPre450;
            _imopVarPre449 = &M;
            _imopVarPre450 = argv[2];
            sscanf(_imopVarPre450, "%d", _imopVarPre449);
            int *_imopVarPre453;
            char *_imopVarPre454;
            _imopVarPre453 = &K;
            _imopVarPre454 = argv[3];
            sscanf(_imopVarPre454, "%d", _imopVarPre453);
        }
        g = generate_new_connected_graph(N, M);
        unsigned long int _imopVarPre456;
        void *_imopVarPre457;
        _imopVarPre456 = N * sizeof(int);
        _imopVarPre457 = malloc(_imopVarPre456);
        kvals = _imopVarPre457;
        int i;
        for (i = 0; i < N; i++) {
            kvals[i] = i;
        }
    }
    long long duration = 0;
    int verification;
    int i;
    for (i = 0; i < iterations; i++) {
        int condVar_imopVarPre2;
        int i_imopVarPre5;
        int condVar_imopVarPre0;
        int i_imopVarPre1;
        int i_imopVarPre4;
        int condVar_imopVarPre7;
        int i_imopVarPre3;
        unsigned long int _imopVarPre459;
        struct stUn_imopVarPre31 *_imopVarPre460;
        _imopVarPre459 = sizeof(int);
        _imopVarPre460 = new_queuelist(N, _imopVarPre459);
        queuelist *active_ql = _imopVarPre460;
        unsigned long int _imopVarPre462;
        struct stUn_imopVarPre31 *_imopVarPre463;
        _imopVarPre462 = sizeof(invitation);
        _imopVarPre463 = new_queuelist(N, _imopVarPre462);
#pragma omp parallel
        {
            queuelist *invite_ql = _imopVarPre463;
#pragma omp master
            {
                begin_timer();
                initialize_graph(g, kvals);
            }
            int k;
            signed long long int _imopVarPre464;
#pragma omp master
            {
                k = 0;
                condVar_imopVarPre7 = (k < K);
            }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
            for (; condVar_imopVarPre7; ) {
                int k_imopVarPre6;
#pragma omp master
                {
                    k_imopVarPre6 = 0;
                    condVar_imopVarPre0 = (k_imopVarPre6 < K - 1);
                }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
                for (; condVar_imopVarPre0; ) {
#pragma omp for nowait
                    for (i_imopVarPre5 = 0; i_imopVarPre5 < g->N; i_imopVarPre5++) {
                        struct stUn_imopVarPre27 *_imopVarPre357;
                        void *_imopVarPre358;
                        _imopVarPre357 = &g->vertices;
                        _imopVarPre358 = elem_at(_imopVarPre357, i_imopVarPre5);
                        node *cur = _imopVarPre358;
                        payload *data = cur->data;
                        if (data->committee == g->N + 1) {
                            data->min_active = data->default_leader;
                        } else {
                            data->min_active = g->N + 1;
                        }
                        int j;
                        for (j = 0; j < cur->degree; j++) {
                            struct stUn_imopVarPre27 *_imopVarPre365;
                            void *_imopVarPre366;
                            _imopVarPre365 = &cur->neighbors;
                            _imopVarPre366 = elem_at(_imopVarPre365, j);
                            node *neighbor = *((node **) _imopVarPre366);
                            int *_imopVarPre369;
                            int _imopVarPre370;
                            _imopVarPre369 = &data->min_active;
                            _imopVarPre370 = neighbor->label;
                            enqueue(active_ql, _imopVarPre370, _imopVarPre369);
                        }
                    }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp for nowait
                    for (i_imopVarPre5 = 0; i_imopVarPre5 < g->N; i_imopVarPre5++) {
                        struct stUn_imopVarPre27 *_imopVarPre372;
                        void *_imopVarPre373;
                        _imopVarPre372 = &g->vertices;
                        _imopVarPre373 = elem_at(_imopVarPre372, i_imopVarPre5);
                        node *cur = _imopVarPre373;
                        payload *data = cur->data;
                        int _imopVarPre375;
                        _imopVarPre375 = is_ql_queue_empty(active_ql, i_imopVarPre5);
                        while (!_imopVarPre375) {
                            void *_imopVarPre376;
                            _imopVarPre376 = dequeue(active_ql, i_imopVarPre5);
                            int *active = _imopVarPre376;
                            int _imopVarPre379;
                            int _imopVarPre380;
                            int _imopVarPre381;
                            _imopVarPre379 = *active;
                            _imopVarPre380 = data->min_active;
                            _imopVarPre381 = min(_imopVarPre380, _imopVarPre379);
                            data->min_active = _imopVarPre381;
                            _imopVarPre375 = is_ql_queue_empty(active_ql, i_imopVarPre5);
                        }
                        int _imopVarPre384;
                        int _imopVarPre385;
                        int _imopVarPre386;
                        _imopVarPre384 = data->min_active;
                        _imopVarPre385 = data->leader;
                        _imopVarPre386 = min(_imopVarPre385, _imopVarPre384);
                        data->leader = _imopVarPre386;
                    }
#pragma omp master
                    {
                        k_imopVarPre6++;
                        condVar_imopVarPre0 = (k_imopVarPre6 < K - 1);
                    }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
                }
                {
                    int k;
#pragma omp for nowait
                    for (i_imopVarPre4 = 0; i_imopVarPre4 < g->N; i_imopVarPre4++) {
                        struct stUn_imopVarPre27 *_imopVarPre388;
                        void *_imopVarPre389;
                        _imopVarPre388 = &g->vertices;
                        _imopVarPre389 = elem_at(_imopVarPre388, i_imopVarPre4);
                        node *cur = _imopVarPre389;
                        payload *data = cur->data;
                        if (data->leader == data->default_leader) {
                            data->invite.x = i_imopVarPre4;
                            data->invite.y = data->min_active;
                        } else {
                            data->invite.x = g->N + 1;
                            data->invite.y = g->N + 1;
                        }
                    }
#pragma omp master
                    {
                        k = 0;
                        condVar_imopVarPre2 = (k < K - 1);
                    }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
                    for (; condVar_imopVarPre2; ) {
#pragma omp for nowait
                        for (i_imopVarPre1 = 0; i_imopVarPre1 < g->N; i_imopVarPre1++) {
                            struct stUn_imopVarPre27 *_imopVarPre391;
                            void *_imopVarPre392;
                            _imopVarPre391 = &g->vertices;
                            _imopVarPre392 = elem_at(_imopVarPre391, i_imopVarPre1);
                            node *cur = _imopVarPre392;
                            payload *data = cur->data;
                            int j;
                            for (j = 0; j < cur->degree; j++) {
                                struct stUn_imopVarPre27 *_imopVarPre399;
                                void *_imopVarPre400;
                                _imopVarPre399 = &cur->neighbors;
                                _imopVarPre400 = elem_at(_imopVarPre399, j);
                                node *neighbor = *((node **) _imopVarPre400);
                                struct stUn_imopVarPre32 *_imopVarPre403;
                                int _imopVarPre404;
                                _imopVarPre403 = &data->invite;
                                _imopVarPre404 = neighbor->label;
                                enqueue(invite_ql, _imopVarPre404, _imopVarPre403);
                            }
                        }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp for nowait
                        for (i_imopVarPre1 = 0; i_imopVarPre1 < g->N; i_imopVarPre1++) {
                            struct stUn_imopVarPre27 *_imopVarPre406;
                            void *_imopVarPre407;
                            _imopVarPre406 = &g->vertices;
                            _imopVarPre407 = elem_at(_imopVarPre406, i_imopVarPre1);
                            node *cur = _imopVarPre407;
                            payload *data = cur->data;
                            int _imopVarPre409;
                            _imopVarPre409 = is_ql_queue_empty(invite_ql, i_imopVarPre1);
                            while (!_imopVarPre409) {
                                void *_imopVarPre410;
                                _imopVarPre410 = dequeue(invite_ql, i_imopVarPre1);
                                invitation *invite = _imopVarPre410;
                                struct stUn_imopVarPre32 *_imopVarPre412;
                                _imopVarPre412 = &data->invite;
                                min_invitation(_imopVarPre412, invite);
                                _imopVarPre409 = is_ql_queue_empty(invite_ql, i_imopVarPre1);
                            }
                            int _imopVarPre414;
                            _imopVarPre414 = data->invite.y == data->default_leader;
                            if (_imopVarPre414) {
                                _imopVarPre414 = data->invite.x == data->leader;
                            }
                            if (_imopVarPre414) {
                                data->committee = data->leader;
                            }
                        }
#pragma omp master
                        {
                            k++;
                            condVar_imopVarPre2 = (k < K - 1);
                        }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
                    }
                }
#pragma omp master
                {
                    k++;
                    condVar_imopVarPre7 = (k < K);
                }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
            }
#pragma omp for nowait
            for (i_imopVarPre3 = 0; i_imopVarPre3 < g->N; i_imopVarPre3++) {
                struct stUn_imopVarPre27 *_imopVarPre416;
                void *_imopVarPre417;
                _imopVarPre416 = &g->vertices;
                _imopVarPre417 = elem_at(_imopVarPre416, i_imopVarPre3);
                node *cur = _imopVarPre417;
                payload *data = cur->data;
                if (data->committee >= g->N) {
                    data->committee = i_imopVarPre3;
                }
            }
// #pragma omp dummyFlush BARRIER_START
#pragma omp barrier
#pragma omp master
            {
                _imopVarPre464 = time_elapsed();
                duration += _imopVarPre464;
                verification = verify_and_print_solution(g, K);
                free_queuelist(invite_ql);
                free_queuelist(active_ql);
            }
        }
    }
    free(kvals);
    if (iterate) {
        double _imopVarPre466;
        _imopVarPre466 = ((double) duration) / iterations;
        printf("%.2lf\n", _imopVarPre466);
    }
    return verification;
}
