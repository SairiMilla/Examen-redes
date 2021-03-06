//Compilar con gcc dict.c servidor.c -o servidor -lpthread
//Ejecutar con ./servidor <puerto>
#include "dict.h"
#include "set.h"
#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <string.h>
#include <arpa/inet.h>
#include <unistd.h>//read
#include <stdlib.h>//exit
#include <netdb.h> //getaddrinfo() getnameinfo() freeaddrinfo()
#include <pthread.h>
#include <semaphore.h>
#define LOCAL_IP "192.168.1.70"
#define MULTI_PORT 12345
#define MULTI_GROUP "225.0.0.37"
#define MSGBUFSIZE 256
#define ANNOUNCEMENT_INTERVAL 3

//Estructura que se le pasa a los hilos
typedef struct dato{
  int sd;
} Descriptor;

Dict d;
Conjunto set;
sem_t semaphore;
sem_t semaphoreSet;
//funcion que lee enteros desde un descriptor de socket
int read_int(int fd){
  int tmp;
  read(fd, &tmp, sizeof(tmp));
  return ntohl(tmp);
}
//funcion que escribe enteros desde un descriptor de socket
void write_int(int fd, int a){
  int tmp = htonl(a);
  write(fd, &tmp, sizeof(tmp));
}
//Funcion que notifica a otro servidor de una insercion
void spreadInsert(char* host, char* pto, char* palabra, char* significado){
  int cd, n, v=1, r;
  struct addrinfo hints, *info, *p;
  memset(&hints, 0, sizeof(hints));
  hints.ai_family = AF_UNSPEC;
  hints.ai_socktype = SOCK_STREAM;
  hints.ai_protocol=0;
  if((r=getaddrinfo(host, pto, &hints, &info))!=0){
    fprintf(stderr, "error:%s" ,gai_strerror(r));
    return;
  }
  for(p=info; p!=NULL; p=p->ai_next){
    if((cd=socket(p->ai_family, p->ai_socktype, p->ai_protocol))<0){
      perror("Error en funcion socket\n");
      continue;
    }
    if(connect(cd, p->ai_addr, p->ai_addrlen)<0){
      close(cd);
      perror("Error en funcion connect()\n");
      continue;
    }
    break;
  }
  freeaddrinfo(info);
  if(cd < 0)
    return;
  write_int(cd, 3);
  write_int(cd, strlen(palabra));
  write_int(cd, strlen(significado));
  write(cd, palabra, strlen(palabra));
  write(cd, significado, strlen(significado));
  close(cd);
}
//Funcion que notifica a un servidor que debe borrar una palabra
void spreadDelete(char* host, char* pto, char* palabra){
  int cd, n, v=1, r;
  struct addrinfo hints, *info, *p;
  memset(&hints, 0, sizeof(hints));
  hints.ai_family = AF_UNSPEC;
  hints.ai_socktype = SOCK_STREAM;
  hints.ai_protocol=0;
  if((r=getaddrinfo(host, pto, &hints, &info))!=0){
    fprintf(stderr, "error:%s" ,gai_strerror(r));
    return;
  }
  for(p=info; p!=NULL; p=p->ai_next){
    if((cd=socket(p->ai_family, p->ai_socktype, p->ai_protocol))<0){
      perror("Error en funcion socket\n");
      continue;
    }
    if(connect(cd, p->ai_addr, p->ai_addrlen)<0){
      close(cd);
      perror("Error en funcion connect()\n");
      continue;
    }
    break;
  }
  freeaddrinfo(info);
  if(cd < 0)
    return;
  write_int(cd, 4);
  write_int(cd, strlen(palabra));
  write(cd, palabra, strlen(palabra));
  close(cd);
}
//Hilo que anuncia el puerto del servidor cada segundo
void *announcer(void *arg){
  struct sockaddr_in addr;
     int fd, cnt;
     struct ip_mreq mreq;
     char *message=(char*)arg;

     /* create what looks like an ordinary UDP socket */
     if ((fd=socket(AF_INET,SOCK_DGRAM,0)) < 0) {
      perror("socket");
      return;
     }

     /* set up destination address */
     memset(&addr,0,sizeof(addr));
     addr.sin_family=AF_INET;
     addr.sin_addr.s_addr=inet_addr(MULTI_GROUP);
     addr.sin_port=htons(MULTI_PORT);
     
     /* now just sendto() our destination! */
     while (1) {
      if (sendto(fd,message,strlen(message),0,(struct sockaddr *) &addr,
             sizeof(addr)) < 0) {
           perror("sendto");
           continue;
      }
      puts("Enviando...");
      sleep(ANNOUNCEMENT_INTERVAL);
     }
}

void *listener(void *arg){
  puts("Entra a listenr");
  struct sockaddr_in addr;
     int fd, nbytes,addrlen;
     struct ip_mreq mreq;
     char msgbuf[MSGBUFSIZE];
     char* pto = (char*)arg;
     u_int yes=1;            /*** MODIFICATION TO ORIGINAL */

     /* create what looks like an ordinary UDP socket */
     if ((fd=socket(AF_INET,SOCK_DGRAM,0)) < 0) {
      perror("socket");
      return;
     }


    /* allow multiple sockets to use the same PORT number */
    if (setsockopt(fd,SOL_SOCKET,SO_REUSEADDR,&yes,sizeof(yes)) < 0) {
       perror("Reusing ADDR failed");
       return;
       }

     /* set up destination address */
     memset(&addr,0,sizeof(addr));
     addr.sin_family=AF_INET;
     addr.sin_addr.s_addr=htonl(INADDR_ANY); /* N.B.: differs from sender */
     addr.sin_port=htons(MULTI_PORT);
     
     /* bind to receive address */
     if (bind(fd,(struct sockaddr *) &addr,sizeof(addr)) < 0) {
      perror("bind");
      return;
     }
     
     /* use setsockopt() to request that the kernel join a multicast group */
     mreq.imr_multiaddr.s_addr=inet_addr(MULTI_GROUP);
     mreq.imr_interface.s_addr=htonl(INADDR_ANY);
     if (setsockopt(fd,IPPROTO_IP,IP_ADD_MEMBERSHIP,&mreq,sizeof(mreq)) < 0) {
      perror("setsockopt");
      return;
     }

     /* now just enter a read-print loop */
     while (1) {
      addrlen=sizeof(addr);
      if ((nbytes=recvfrom(fd,msgbuf,MSGBUFSIZE,0,
                   (struct sockaddr *) &addr,&addrlen)) < 0) {
           perror("recvfrom");
           return;
      }
      char* ipString = inet_ntoa(addr.sin_addr);

      if(nbytes==4){
        char newPto[5];
        strncpy(newPto, msgbuf, nbytes);
        printf("Recibiendo paquete desde %s: %s\n",ipString, newPto);
        if(!(strcmp(pto, newPto)==0 && strcmp(ipString, LOCAL_IP)==0)){
          printf("Conjunto actual: ");
          sem_wait(&semaphoreSet);
          set = inserta(newPto, ipString, set);
          imprimeCon(set);
          sem_post(&semaphoreSet);
        }
      }
  }
}

// get sockaddr, IPv4 or IPv6:
void *get_in_addr(struct sockaddr *sa)
{
    if (sa->sa_family == AF_INET) {
        return &(((struct sockaddr_in*)sa)->sin_addr);
    }

    return &(((struct sockaddr_in6*)sa)->sin6_addr);
}



//funcion del hilo
void* clientHandler(void* args){
  Descriptor *des = (Descriptor*)args;
  int temp;
  int received, sent;
  char cTmp;
  received =  read(des->sd, &temp, sizeof(temp));
  int operacion = ntohl(temp);
  printf("Operacion: %d\n", operacion);
  if(operacion<=2){
    if(sem_trywait(&semaphore)==0){
      if(operacion==0){
        int size_palabra = read_int(des->sd);
        int size_significado = read_int(des->sd);
        char palabra[size_palabra + 1];
        palabra[size_palabra] = '\0';
        char significado[size_significado + 1];
        significado[size_significado] = '\0';
        read(des->sd, palabra, size_palabra);
        read(des->sd, significado, size_significado);
        DictShow(d);
        printf("Insertando %s que es %s\n", palabra, significado);
        DictInsert(d, palabra, significado);
        DictShow(d);
        write_int(des->sd, 0);
        close(des->sd);
        sem_post(&semaphore);
        sem_wait(&semaphoreSet);
        Conjunto aux = set;
        while(!esVacio(aux)){
          spreadInsert(aux->ip, aux->pto, palabra, significado);
          aux = aux->sig;
        }
        sem_post(&semaphoreSet);
      }  
      else if(operacion==1){
        int size_palabra = read_int(des->sd);
        char palabra[size_palabra + 1];
        palabra[size_palabra] = '\0';
        read(des->sd, palabra, size_palabra );
        DictShow(d);
        printf("Intentando borrar: %s...\n", palabra);
        DictDelete(d, palabra);
        DictShow(d);
        write_int(des->sd, 0);
        close(des->sd);
        sem_post(&semaphore);
        sem_wait(&semaphoreSet);
        Conjunto aux = set;
        while(!esVacio(aux)){
          spreadDelete(aux->ip, aux->pto, palabra);
          aux = aux->sig;
        }
        sem_post(&semaphoreSet);
      }
      else if(operacion==2){
        printf("Mandando todo el diccionario...");
        write_int(des->sd, DictSize(d));
        DictList(d, des->sd);
        close(des->sd);
        sem_post(&semaphore);
      }
      printf("Cliente listo\n");  
    }
    else{
      close(des->sd);
      printf("Cliente rechazado\n");
    }
  }
  else if(operacion==3){
    puts("Llega operacion de insercion por parte de un servidor");
    int size_palabra = read_int(des->sd);
    int size_significado = read_int(des->sd);
    char palabra[size_palabra + 1];
    palabra[size_palabra] = '\0';
    char significado[size_significado + 1];
    significado[size_significado] = '\0';
    read(des->sd, palabra, size_palabra);
    read(des->sd, significado, size_significado);
    DictShow(d);
    printf("Insertando %s que es %s\n", palabra, significado);
    DictInsert(d, palabra, significado);
    DictShow(d);
    close(des->sd);
  }
  else if(operacion==4){
    puts("Llega operacion de borrar por parte de un servidor");
    int size_palabra = read_int(des->sd);
    char palabra[size_palabra + 1];
    palabra[size_palabra] = '\0';
    read(des->sd, palabra, size_palabra );
    DictShow(d);
    printf("Intentando borrar: %s...\n", palabra);
    DictDelete(d, palabra);
    DictShow(d);
    close(des->sd);
  }
}

int main(int argc, char* argv[]){
  //Creacion del diccionario
  if(argc < 2){
    printf("Error. Uso ./servidor <puerto>\n");
    return 0;
  }
  d = DictCreate();
  set = vacio();
  DictInsert(d, "pokemon", "monstruo de bolsillo");
  DictInsert(d, "roca", "un pedazo de tierra duro" );
  DictInsert(d, "fuego", "una cosa que te quema");
  sem_init(&semaphore, 0, 3);
  sem_init(&semaphoreSet, 0, 1);
  //Creacion del hilo que anuncia al servidor
  pthread_t t_announcer;
  pthread_create(&t_announcer, NULL, &announcer, (void*)argv[1]);
  pthread_t t_listener;
  pthread_create(&t_listener, NULL, &listener, (void*)argv[1]);
  //Funciones y estructuras necesarias para levantar el servidor
 int sd,n,n1,v=1,rv,op=0, *new_sock, cd;
 socklen_t ctam;
 char s[INET6_ADDRSTRLEN], hbuf[NI_MAXHOST], sbuf[NI_MAXSERV];
 //struct sockaddr_in sdir,cdir;
 struct addrinfo hints, *servinfo, *p;
 struct sockaddr_storage their_addr; // connector's address 
 ctam= sizeof(their_addr);
 memset(&hints, 0, sizeof (hints));  //indicio
 hints.ai_family = AF_INET6;    /* Allow IPv4 or IPv6  familia de dir*/
 hints.ai_socktype = SOCK_STREAM;
 hints.ai_flags = AI_PASSIVE; // use my IP
 hints.ai_protocol = 0;          /* Any protocol */
 hints.ai_canonname = NULL;
 hints.ai_addr = NULL;
 hints.ai_next = NULL;
 printf("%s\n", argv[1]);
 if ((rv = getaddrinfo(NULL, argv[1], &hints, &servinfo)) != 0) {
     fprintf(stderr, "getaddrinfo: %s\n", gai_strerror(rv));
     return 1;
 }//if

    for(p = servinfo; p != NULL; p = p->ai_next) {
        if ((sd = socket(p->ai_family, p->ai_socktype,p->ai_protocol)) == -1) {
            perror("server: socket");
            continue;
        }

        if (setsockopt(sd, SOL_SOCKET, SO_REUSEADDR, &v,sizeof(int)) == -1) {
            perror("setsockopt");
            exit(1);
        }

  if (setsockopt(sd, IPPROTO_IPV6, IPV6_V6ONLY, (void *)&op, sizeof(op)) == -1) {
            perror("setsockopt   no soporta IPv6");
            exit(1);
        }

        if (bind(sd, p->ai_addr, p->ai_addrlen) == -1) {
            close(sd);
            perror("server: bind");
            continue;
        }//if

        break;
    }//for

    freeaddrinfo(servinfo); // all done with this structure

    if (p == NULL)  {
        fprintf(stderr, "servidor: error en bind\n");
        exit(1);
    }

   listen(sd,5);
   printf("Servidor listo.. Esperando clientes \n");
  
  for(;;){
  
    ctam = sizeof their_addr;
    int cd = accept(sd, (struct sockaddr *)&their_addr, &ctam);
    if (cd == -1) {
      perror("accept");
      continue;
    }
    if (getnameinfo((struct sockaddr *)&their_addr, sizeof(their_addr), hbuf, sizeof(hbuf), sbuf,sizeof(sbuf), NI_NUMERICHOST | NI_NUMERICSERV) == 0)
      printf("cliente conectado desde %s:%s\n", hbuf,sbuf);
    pthread_t t;
    Descriptor *des = (Descriptor*)malloc(sizeof(Descriptor));
    des->sd = cd;
    pthread_create(&t, NULL, clientHandler, des);
  }//for
close(sd);
return 0;
}//main
