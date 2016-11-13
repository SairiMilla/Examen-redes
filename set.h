#include <string.h>
#include <stdlib.h>
#include <stdio.h>
typedef struct nodo
{
	char *pto;
	char *ip;
	struct nodo *sig;
}*Conjunto;

Conjunto vacio()
{
	return NULL;
}

int estaEn(char* pto, char* ip, Conjunto c)
{
	if(esVacio(c))
		return 0;
	else
	{
		if(strcmp(pto,c->pto)==0 && strcmp(ip, c->ip)==0)
			return 1;
		else
			return estaEn(pto, ip,c->sig);
	}
}

Conjunto inserta(char* pto, char* ip, Conjunto c)
{
	if(!estaEn(pto, ip ,c))
	{
		Conjunto aux = (Conjunto)malloc(sizeof(struct nodo));
		aux->ip = (char*)malloc(strlen(ip));
		strcpy(aux->ip, ip);
		aux->pto = (char*)malloc(strlen(pto));
		strcpy(aux->pto, pto);
		aux -> sig = c;
		return aux;
	}
	else
		return c;
}

int esVacio(Conjunto c)
{
	return c==NULL;
}


void imprimeCon(Conjunto c)
{
	if(!esVacio(c))
	{
		printf("%s:%s ", c->ip, c->pto);
		imprimeCon(c->sig);
	}
	else
		printf("\n");
}