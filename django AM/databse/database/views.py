from django.shortcuts import render
from database.models import Account, Ranking
from django.http import HttpResponse
# Create your views here.
from django.views.decorators.csrf import csrf_exempt
from django.core import serializers

@csrf_exempt
def login(request):

    if 'login' in request.POST and 'password' in request.POST:
        account = Account.objects.filter(login=request.POST['login'])
        if(len(account) == 1):
            account = account[0]
            if account.password == request.POST['password']:
                return HttpResponse("OK")

    return HttpResponse("Fail")

@csrf_exempt
def singup(request):
    try:

        login = request.POST['login']
        password = request.POST['password']

        if len(Account.objects.filter(login=login)) == 0:
            account = Account(login=login, password=password)
            account.save()
            ranking = Ranking(login=account, score=0)
            ranking.save()
            return HttpResponse("OK")
    except:
        pass

    return HttpResponse("Fail")
@csrf_exempt
def ranking(request):
    res = "["
    for item in Ranking.objects.all().order_by('-score'):
        res += str(item)+","
    res = res[:-1]+ "]"
    if(len(res)==1):
        res = "[]"


    return HttpResponse(res, content_type="text/json-comment-filtered")

@csrf_exempt
def clear(request):
    Account.objects.all().delete()
    return HttpResponse("")

@csrf_exempt
def update(request):

    login = request.POST['login']
    score = int(request.POST['score'])

    user = Account.objects.filter(login=login)[0]
    ranking = Ranking.objects.filter(login=user)[0]

    if(ranking.score<score):
        ranking.score = score
    ranking.save()


    return HttpResponse("")