from django.db import models

# Create your models here.

class Account(models.Model):
    login = models.CharField(max_length=20)
    password = models.CharField(max_length=20)

class Ranking(models.Model):
    login = models.ForeignKey(Account, on_delete=models.CASCADE)
    score = models.IntegerField()

    def __str__(self):
        return '{"user":"'+self.login.login+'", "score":'+str(self.score)+"}"